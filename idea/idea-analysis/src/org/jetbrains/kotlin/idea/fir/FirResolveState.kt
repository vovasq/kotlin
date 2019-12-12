/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.fir

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.fir.*
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.java.FirLibrarySession
import org.jetbrains.kotlin.fir.java.FirProjectSessionProvider
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeDiagnostic
import org.jetbrains.kotlin.idea.caches.project.IdeaModuleInfo
import org.jetbrains.kotlin.idea.caches.project.ModuleSourceInfo
import org.jetbrains.kotlin.idea.caches.project.getModuleInfo
import org.jetbrains.kotlin.idea.caches.project.isLibraryClasses
import org.jetbrains.kotlin.idea.caches.resolve.IDEPackagePartProvider
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.cast

private fun createLibrarySession(moduleInfo: IdeaModuleInfo, project: Project, provider: FirProjectSessionProvider): FirLibrarySession {
    val contentScope = moduleInfo.contentScope()
    return FirLibrarySession.create(moduleInfo, provider, contentScope, project, IDEPackagePartProvider(contentScope))
}

private fun getOrCreateIdeSession(
    sessionProvider: FirProjectSessionProvider,
    project: Project,
    moduleInfo: ModuleSourceInfo
): FirSession {
    sessionProvider.getSession(moduleInfo)?.let { return it }
    return synchronized(moduleInfo.module) {
        sessionProvider.getSession(moduleInfo) ?: FirIdeJavaModuleBasedSession(
            project, moduleInfo, sessionProvider, moduleInfo.contentScope()
        ).also { moduleBasedSession ->
            val ideaModuleInfo = moduleInfo.cast<IdeaModuleInfo>()
            ideaModuleInfo.dependenciesWithoutSelf().forEach {
                if (it is IdeaModuleInfo && it.isLibraryClasses()) {
                    // TODO: consider caching / synchronization here
                    createLibrarySession(it, project, sessionProvider)
                }
            }
            sessionProvider.sessionCache[moduleInfo] = moduleBasedSession
        }
    }
}

interface FirResolveState {
    val sessionProvider: FirSessionProvider

    fun getSession(psi: KtElement): FirSession {
        val sessionProvider = sessionProvider as FirProjectSessionProvider
        val moduleInfo = psi.getModuleInfo() as ModuleSourceInfo
        return getOrCreateIdeSession(sessionProvider, psi.project, moduleInfo)
    }

    operator fun get(psi: KtElement): FirElement?

    fun getDiagnostics(psi: KtElement): List<Diagnostic>

    fun hasDiagnosticsForFile(file: KtFile): Boolean

    fun record(psi: KtElement, fir: FirElement)

    fun record(psi: KtElement, diagnostic: Diagnostic)

    fun setDiagnosticsForFile(file: KtFile, fir: FirFile, diagnostics: Iterable<ConeDiagnostic> = emptyList())
}

class FirResolveStateImpl(override val sessionProvider: FirSessionProvider) : FirResolveState {
    private val cache = mutableMapOf<KtElement, FirElement>()

    private val diagnosticCache = mutableMapOf<KtElement, MutableList<Diagnostic>>()

    private val diagnosedFiles = mutableMapOf<KtFile, Long>()

    override fun get(psi: KtElement): FirElement? = cache[psi]

    override fun getDiagnostics(psi: KtElement): List<Diagnostic> {
        return diagnosticCache[psi] ?: emptyList()
    }

    override fun hasDiagnosticsForFile(file: KtFile): Boolean {
        val previousStamp = diagnosedFiles[file] ?: return false
        if (file.modificationStamp == previousStamp) {
            return true
        }
        diagnosedFiles.remove(file)
        file.accept(object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                cache.remove(element)
                diagnosticCache.remove(element)
                element.acceptChildren(this)
                super.visitElement(element)
            }
        })
        return false
    }

    override fun record(psi: KtElement, fir: FirElement) {
        cache[psi] = fir
    }

    override fun record(psi: KtElement, diagnostic: Diagnostic) {
        val list = diagnosticCache.getOrPut(psi) { mutableListOf() }
        list += diagnostic
    }

    override fun setDiagnosticsForFile(file: KtFile, fir: FirFile, diagnostics: Iterable<ConeDiagnostic>) {
        for (diagnostic in diagnostics) {
            (diagnostic.source.psi as? KtElement)?.let { record(it, diagnostic.diagnostic) }
        }

        diagnosedFiles[file] = file.modificationStamp
    }
}

fun KtElement.firResolveState(): FirResolveState =
    FirIdeResolveStateService.getInstance(project).getResolveState(getModuleInfo())