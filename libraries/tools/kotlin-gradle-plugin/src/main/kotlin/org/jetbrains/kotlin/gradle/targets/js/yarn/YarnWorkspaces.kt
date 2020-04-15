/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.yarn

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmApi
import org.jetbrains.kotlin.gradle.targets.js.npm.PackageJson
import org.jetbrains.kotlin.gradle.targets.js.npm.resolved.KotlinCompilationNpmResolution
import java.io.File

class YarnWorkspaces : YarnBasics() {
    override fun resolveProject(resolvedNpmProject: KotlinCompilationNpmResolution) = Unit

    override fun prepareRootProject(
        rootProject: Project,
        subProjects: Collection<KotlinCompilationNpmResolution>,
        skipExecution: Boolean,
        cliArgs: List<String>
    ) {
        check(rootProject == rootProject.rootProject)
        if (!skipExecution) setup(rootProject)
        return prepareRootPackageJson(
            rootProject,
            subProjects,
            skipExecution
        )
    }

    private fun prepareRootPackageJson(
        rootProject: Project,
        npmProjects: Collection<KotlinCompilationNpmResolution>,
        skipExecution: Boolean
    ) {
        val nodeJs = NodeJsRootPlugin.apply(rootProject)
        val rootPackageJsonFile = nodeJs.rootPackageJson

        if (!skipExecution) {
            saveRootProjectWorkspacesPackageJson(rootProject, npmProjects, rootPackageJsonFile)
        }
    }

    override fun resolveRootProject(
        rootProject: Project,
        npmProjects: Collection<KotlinCompilationNpmResolution>,
        skipExecution: Boolean,
        cliArgs: List<String>
    ) {
        val nodeJs = NodeJsRootPlugin.apply(rootProject)
        val nodeJsWorldDir = nodeJs.rootPackageDir

        yarnExec(
            rootProject,
            nodeJsWorldDir,
            NpmApi.resolveOperationDescription("yarn"),
            cliArgs
        )
        nodeJs.rootNodeModulesStateFile.writeText(System.currentTimeMillis().toString())

        yarnLockReadTransitiveDependencies(nodeJsWorldDir, npmProjects.flatMap { it.externalNpmDependencies })
    }

    private fun saveRootProjectWorkspacesPackageJson(
        rootProject: Project,
        npmProjects: Collection<KotlinCompilationNpmResolution>,
        rootPackageJsonFile: File
    ) {
        val nodeJsWorldDir = rootPackageJsonFile.parentFile
        val rootPackageJson = PackageJson(rootProject.name, rootProject.version.toString())
        rootPackageJson.private = true

        val npmProjectWorkspaces = npmProjects.map { it.npmProject.dir.relativeTo(nodeJsWorldDir).path }
        val importedProjectWorkspaces =
            YarnImportedPackagesVersionResolver(rootProject, npmProjects, nodeJsWorldDir).resolveAndUpdatePackages()

        rootPackageJson.workspaces = npmProjectWorkspaces + importedProjectWorkspaces
        rootPackageJson.saveTo(
            rootPackageJsonFile
        )
    }
}