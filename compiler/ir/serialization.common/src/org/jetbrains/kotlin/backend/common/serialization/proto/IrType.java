// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: compiler/ir/serialization.common/src/KotlinIr.proto

package org.jetbrains.kotlin.backend.common.serialization.proto;

/**
 * Protobuf type {@code org.jetbrains.kotlin.backend.common.serialization.proto.IrType}
 */
public final class IrType extends
    org.jetbrains.kotlin.protobuf.GeneratedMessageLite implements
    // @@protoc_insertion_point(message_implements:org.jetbrains.kotlin.backend.common.serialization.proto.IrType)
    IrTypeOrBuilder {
  // Use IrType.newBuilder() to construct.
  private IrType(org.jetbrains.kotlin.protobuf.GeneratedMessageLite.Builder builder) {
    super(builder);
    this.unknownFields = builder.getUnknownFields();
  }
  private IrType(boolean noInit) { this.unknownFields = org.jetbrains.kotlin.protobuf.ByteString.EMPTY;}

  private static final IrType defaultInstance;
  public static IrType getDefaultInstance() {
    return defaultInstance;
  }

  public IrType getDefaultInstanceForType() {
    return defaultInstance;
  }

  private final org.jetbrains.kotlin.protobuf.ByteString unknownFields;
  private IrType(
      org.jetbrains.kotlin.protobuf.CodedInputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    initFields();
    int mutable_bitField0_ = 0;
    org.jetbrains.kotlin.protobuf.ByteString.Output unknownFieldsOutput =
        org.jetbrains.kotlin.protobuf.ByteString.newOutput();
    org.jetbrains.kotlin.protobuf.CodedOutputStream unknownFieldsCodedOutput =
        org.jetbrains.kotlin.protobuf.CodedOutputStream.newInstance(
            unknownFieldsOutput, 1);
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownField(input, unknownFieldsCodedOutput,
                                   extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.Builder subBuilder = null;
            if (kindCase_ == 1) {
              subBuilder = ((org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_).toBuilder();
            }
            kind_ = input.readMessage(org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_);
              kind_ = subBuilder.buildPartial();
            }
            kindCase_ = 1;
            break;
          }
          case 18: {
            org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.Builder subBuilder = null;
            if (kindCase_ == 2) {
              subBuilder = ((org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_).toBuilder();
            }
            kind_ = input.readMessage(org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_);
              kind_ = subBuilder.buildPartial();
            }
            kindCase_ = 2;
            break;
          }
          case 26: {
            org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.Builder subBuilder = null;
            if (kindCase_ == 3) {
              subBuilder = ((org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_).toBuilder();
            }
            kind_ = input.readMessage(org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_);
              kind_ = subBuilder.buildPartial();
            }
            kindCase_ = 3;
            break;
          }
        }
      }
    } catch (org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException(
          e.getMessage()).setUnfinishedMessage(this);
    } finally {
      try {
        unknownFieldsCodedOutput.flush();
      } catch (java.io.IOException e) {
      // Should not happen
      } finally {
        unknownFields = unknownFieldsOutput.toByteString();
      }
      makeExtensionsImmutable();
    }
  }
  public static org.jetbrains.kotlin.protobuf.Parser<IrType> PARSER =
      new org.jetbrains.kotlin.protobuf.AbstractParser<IrType>() {
    public IrType parsePartialFrom(
        org.jetbrains.kotlin.protobuf.CodedInputStream input,
        org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
        throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
      return new IrType(input, extensionRegistry);
    }
  };

  @java.lang.Override
  public org.jetbrains.kotlin.protobuf.Parser<IrType> getParserForType() {
    return PARSER;
  }

  private int bitField0_;
  private int kindCase_ = 0;
  private java.lang.Object kind_;
  public enum KindCase
      implements org.jetbrains.kotlin.protobuf.Internal.EnumLite {
    SIMPLE(1),
    DYNAMIC(2),
    ERROR(3),
    KIND_NOT_SET(0);
    private int value = 0;
    private KindCase(int value) {
      this.value = value;
    }
    public static KindCase valueOf(int value) {
      switch (value) {
        case 1: return SIMPLE;
        case 2: return DYNAMIC;
        case 3: return ERROR;
        case 0: return KIND_NOT_SET;
        default: throw new java.lang.IllegalArgumentException(
          "Value is undefined for this oneof enum.");
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public KindCase
  getKindCase() {
    return KindCase.valueOf(
        kindCase_);
  }

  public static final int SIMPLE_FIELD_NUMBER = 1;
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
   */
  public boolean hasSimple() {
    return kindCase_ == 1;
  }
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
   */
  public org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType getSimple() {
    if (kindCase_ == 1) {
       return (org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_;
    }
    return org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.getDefaultInstance();
  }

  public static final int DYNAMIC_FIELD_NUMBER = 2;
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
   */
  public boolean hasDynamic() {
    return kindCase_ == 2;
  }
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
   */
  public org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType getDynamic() {
    if (kindCase_ == 2) {
       return (org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_;
    }
    return org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.getDefaultInstance();
  }

  public static final int ERROR_FIELD_NUMBER = 3;
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
   */
  public boolean hasError() {
    return kindCase_ == 3;
  }
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
   */
  public org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType getError() {
    if (kindCase_ == 3) {
       return (org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_;
    }
    return org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.getDefaultInstance();
  }

  private void initFields() {
  }
  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (hasSimple()) {
      if (!getSimple().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
    }
    if (hasDynamic()) {
      if (!getDynamic().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
    }
    if (hasError()) {
      if (!getError().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
    }
    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(org.jetbrains.kotlin.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    getSerializedSize();
    if (kindCase_ == 1) {
      output.writeMessage(1, (org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_);
    }
    if (kindCase_ == 2) {
      output.writeMessage(2, (org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_);
    }
    if (kindCase_ == 3) {
      output.writeMessage(3, (org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_);
    }
    output.writeRawBytes(unknownFields);
  }

  private int memoizedSerializedSize = -1;
  public int getSerializedSize() {
    int size = memoizedSerializedSize;
    if (size != -1) return size;

    size = 0;
    if (kindCase_ == 1) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeMessageSize(1, (org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_);
    }
    if (kindCase_ == 2) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeMessageSize(2, (org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_);
    }
    if (kindCase_ == 3) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeMessageSize(3, (org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_);
    }
    size += unknownFields.size();
    memoizedSerializedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  protected java.lang.Object writeReplace()
      throws java.io.ObjectStreamException {
    return super.writeReplace();
  }

  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(
      org.jetbrains.kotlin.protobuf.ByteString data)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(
      org.jetbrains.kotlin.protobuf.ByteString data,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(byte[] data)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(
      byte[] data,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(
      java.io.InputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseDelimitedFrom(
      java.io.InputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(
      org.jetbrains.kotlin.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrType parseFrom(
      org.jetbrains.kotlin.protobuf.CodedInputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public static Builder newBuilder() { return Builder.create(); }
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder(org.jetbrains.kotlin.backend.common.serialization.proto.IrType prototype) {
    return newBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() { return newBuilder(this); }

  /**
   * Protobuf type {@code org.jetbrains.kotlin.backend.common.serialization.proto.IrType}
   */
  public static final class Builder extends
      org.jetbrains.kotlin.protobuf.GeneratedMessageLite.Builder<
        org.jetbrains.kotlin.backend.common.serialization.proto.IrType, Builder>
      implements
      // @@protoc_insertion_point(builder_implements:org.jetbrains.kotlin.backend.common.serialization.proto.IrType)
      org.jetbrains.kotlin.backend.common.serialization.proto.IrTypeOrBuilder {
    // Construct using org.jetbrains.kotlin.backend.common.serialization.proto.IrType.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
    }
    private static Builder create() {
      return new Builder();
    }

    public Builder clear() {
      super.clear();
      kindCase_ = 0;
      kind_ = null;
      return this;
    }

    public Builder clone() {
      return create().mergeFrom(buildPartial());
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.IrType getDefaultInstanceForType() {
      return org.jetbrains.kotlin.backend.common.serialization.proto.IrType.getDefaultInstance();
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.IrType build() {
      org.jetbrains.kotlin.backend.common.serialization.proto.IrType result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.IrType buildPartial() {
      org.jetbrains.kotlin.backend.common.serialization.proto.IrType result = new org.jetbrains.kotlin.backend.common.serialization.proto.IrType(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (kindCase_ == 1) {
        result.kind_ = kind_;
      }
      if (kindCase_ == 2) {
        result.kind_ = kind_;
      }
      if (kindCase_ == 3) {
        result.kind_ = kind_;
      }
      result.bitField0_ = to_bitField0_;
      result.kindCase_ = kindCase_;
      return result;
    }

    public Builder mergeFrom(org.jetbrains.kotlin.backend.common.serialization.proto.IrType other) {
      if (other == org.jetbrains.kotlin.backend.common.serialization.proto.IrType.getDefaultInstance()) return this;
      switch (other.getKindCase()) {
        case SIMPLE: {
          mergeSimple(other.getSimple());
          break;
        }
        case DYNAMIC: {
          mergeDynamic(other.getDynamic());
          break;
        }
        case ERROR: {
          mergeError(other.getError());
          break;
        }
        case KIND_NOT_SET: {
          break;
        }
      }
      setUnknownFields(
          getUnknownFields().concat(other.unknownFields));
      return this;
    }

    public final boolean isInitialized() {
      if (hasSimple()) {
        if (!getSimple().isInitialized()) {
          
          return false;
        }
      }
      if (hasDynamic()) {
        if (!getDynamic().isInitialized()) {
          
          return false;
        }
      }
      if (hasError()) {
        if (!getError().isInitialized()) {
          
          return false;
        }
      }
      return true;
    }

    public Builder mergeFrom(
        org.jetbrains.kotlin.protobuf.CodedInputStream input,
        org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.jetbrains.kotlin.backend.common.serialization.proto.IrType parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.kotlin.backend.common.serialization.proto.IrType) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int kindCase_ = 0;
    private java.lang.Object kind_;
    public KindCase
        getKindCase() {
      return KindCase.valueOf(
          kindCase_);
    }

    public Builder clearKind() {
      kindCase_ = 0;
      kind_ = null;
      return this;
    }

    private int bitField0_;

    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
     */
    public boolean hasSimple() {
      return kindCase_ == 1;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
     */
    public org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType getSimple() {
      if (kindCase_ == 1) {
        return (org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_;
      }
      return org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.getDefaultInstance();
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
     */
    public Builder setSimple(org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      kind_ = value;

      kindCase_ = 1;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
     */
    public Builder setSimple(
        org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.Builder builderForValue) {
      kind_ = builderForValue.build();

      kindCase_ = 1;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
     */
    public Builder mergeSimple(org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType value) {
      if (kindCase_ == 1 &&
          kind_ != org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.getDefaultInstance()) {
        kind_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType.newBuilder((org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType) kind_)
            .mergeFrom(value).buildPartial();
      } else {
        kind_ = value;
      }

      kindCase_ = 1;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType simple = 1;</code>
     */
    public Builder clearSimple() {
      if (kindCase_ == 1) {
        kindCase_ = 0;
        kind_ = null;
        
      }
      return this;
    }

    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
     */
    public boolean hasDynamic() {
      return kindCase_ == 2;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
     */
    public org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType getDynamic() {
      if (kindCase_ == 2) {
        return (org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_;
      }
      return org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.getDefaultInstance();
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
     */
    public Builder setDynamic(org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      kind_ = value;

      kindCase_ = 2;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
     */
    public Builder setDynamic(
        org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.Builder builderForValue) {
      kind_ = builderForValue.build();

      kindCase_ = 2;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
     */
    public Builder mergeDynamic(org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType value) {
      if (kindCase_ == 2 &&
          kind_ != org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.getDefaultInstance()) {
        kind_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType.newBuilder((org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType) kind_)
            .mergeFrom(value).buildPartial();
      } else {
        kind_ = value;
      }

      kindCase_ = 2;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType dynamic = 2;</code>
     */
    public Builder clearDynamic() {
      if (kindCase_ == 2) {
        kindCase_ = 0;
        kind_ = null;
        
      }
      return this;
    }

    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
     */
    public boolean hasError() {
      return kindCase_ == 3;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
     */
    public org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType getError() {
      if (kindCase_ == 3) {
        return (org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_;
      }
      return org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.getDefaultInstance();
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
     */
    public Builder setError(org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      kind_ = value;

      kindCase_ = 3;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
     */
    public Builder setError(
        org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.Builder builderForValue) {
      kind_ = builderForValue.build();

      kindCase_ = 3;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
     */
    public Builder mergeError(org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType value) {
      if (kindCase_ == 3 &&
          kind_ != org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.getDefaultInstance()) {
        kind_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType.newBuilder((org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType) kind_)
            .mergeFrom(value).buildPartial();
      } else {
        kind_ = value;
      }

      kindCase_ = 3;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType error = 3;</code>
     */
    public Builder clearError() {
      if (kindCase_ == 3) {
        kindCase_ = 0;
        kind_ = null;
        
      }
      return this;
    }

    // @@protoc_insertion_point(builder_scope:org.jetbrains.kotlin.backend.common.serialization.proto.IrType)
  }

  static {
    defaultInstance = new IrType(true);
    defaultInstance.initFields();
  }

  // @@protoc_insertion_point(class_scope:org.jetbrains.kotlin.backend.common.serialization.proto.IrType)
}
