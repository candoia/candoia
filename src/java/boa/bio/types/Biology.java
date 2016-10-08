// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Biology.proto

package boa.bio.types;

public final class Biology {
  private Biology() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface BiologyDatasetOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // repeated .boa.bio.types.BioData bio = 1;
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    java.util.List<Bio.BioData>
        getBioList();
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    Bio.BioData getBio(int index);
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    int getBioCount();
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    java.util.List<? extends Bio.BioDataOrBuilder>
        getBioOrBuilderList();
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    Bio.BioDataOrBuilder getBioOrBuilder(
            int index);
  }
  /**
   * Protobuf type {@code boa.bio.types.BiologyDataset}
   */
  public static final class BiologyDataset extends
      com.google.protobuf.GeneratedMessage
      implements BiologyDatasetOrBuilder {
    // Use BiologyDataset.newBuilder() to construct.
    private BiologyDataset(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private BiologyDataset(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final BiologyDataset defaultInstance;
    public static BiologyDataset getDefaultInstance() {
      return defaultInstance;
    }

    public BiologyDataset getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private BiologyDataset(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                bio_ = new java.util.ArrayList<Bio.BioData>();
                mutable_bitField0_ |= 0x00000001;
              }
              bio_.add(input.readMessage(Bio.BioData.PARSER, extensionRegistry));
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
          bio_ = java.util.Collections.unmodifiableList(bio_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return Biology.internal_static_boa_bio_types_BiologyDataset_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return Biology.internal_static_boa_bio_types_BiologyDataset_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              BiologyDataset.class, Builder.class);
    }

    public static com.google.protobuf.Parser<BiologyDataset> PARSER =
        new com.google.protobuf.AbstractParser<BiologyDataset>() {
      public BiologyDataset parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new BiologyDataset(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<BiologyDataset> getParserForType() {
      return PARSER;
    }

    // repeated .boa.bio.types.BioData bio = 1;
    public static final int BIO_FIELD_NUMBER = 1;
    private java.util.List<Bio.BioData> bio_;
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    public java.util.List<Bio.BioData> getBioList() {
      return bio_;
    }
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    public java.util.List<? extends Bio.BioDataOrBuilder>
        getBioOrBuilderList() {
      return bio_;
    }
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    public int getBioCount() {
      return bio_.size();
    }
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    public Bio.BioData getBio(int index) {
      return bio_.get(index);
    }
    /**
     * <code>repeated .boa.bio.types.BioData bio = 1;</code>
     */
    public Bio.BioDataOrBuilder getBioOrBuilder(
        int index) {
      return bio_.get(index);
    }

    private void initFields() {
      bio_ = java.util.Collections.emptyList();
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      for (int i = 0; i < getBioCount(); i++) {
        if (!getBio(i).isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      for (int i = 0; i < bio_.size(); i++) {
        output.writeMessage(1, bio_.get(i));
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < bio_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, bio_.get(i));
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    protected Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static BiologyDataset parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BiologyDataset parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BiologyDataset parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BiologyDataset parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BiologyDataset parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static BiologyDataset parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static BiologyDataset parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static BiologyDataset parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static BiologyDataset parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static BiologyDataset parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(BiologyDataset prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code boa.bio.types.BiologyDataset}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements BiologyDatasetOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return Biology.internal_static_boa_bio_types_BiologyDataset_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return Biology.internal_static_boa_bio_types_BiologyDataset_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                BiologyDataset.class, Builder.class);
      }

      // Construct using boa.bio.types.Biology.BiologyDataset.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getBioFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        if (bioBuilder_ == null) {
          bio_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          bioBuilder_.clear();
        }
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return Biology.internal_static_boa_bio_types_BiologyDataset_descriptor;
      }

      public BiologyDataset getDefaultInstanceForType() {
        return BiologyDataset.getDefaultInstance();
      }

      public BiologyDataset build() {
        BiologyDataset result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public BiologyDataset buildPartial() {
        BiologyDataset result = new BiologyDataset(this);
        int from_bitField0_ = bitField0_;
        if (bioBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001)) {
            bio_ = java.util.Collections.unmodifiableList(bio_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.bio_ = bio_;
        } else {
          result.bio_ = bioBuilder_.build();
        }
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof BiologyDataset) {
          return mergeFrom((BiologyDataset)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(BiologyDataset other) {
        if (other == BiologyDataset.getDefaultInstance()) return this;
        if (bioBuilder_ == null) {
          if (!other.bio_.isEmpty()) {
            if (bio_.isEmpty()) {
              bio_ = other.bio_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureBioIsMutable();
              bio_.addAll(other.bio_);
            }
            onChanged();
          }
        } else {
          if (!other.bio_.isEmpty()) {
            if (bioBuilder_.isEmpty()) {
              bioBuilder_.dispose();
              bioBuilder_ = null;
              bio_ = other.bio_;
              bitField0_ = (bitField0_ & ~0x00000001);
              bioBuilder_ = 
                com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ?
                   getBioFieldBuilder() : null;
            } else {
              bioBuilder_.addAllMessages(other.bio_);
            }
          }
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        for (int i = 0; i < getBioCount(); i++) {
          if (!getBio(i).isInitialized()) {
            
            return false;
          }
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        BiologyDataset parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (BiologyDataset) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // repeated .boa.bio.types.BioData bio = 1;
      private java.util.List<Bio.BioData> bio_ =
        java.util.Collections.emptyList();
      private void ensureBioIsMutable() {
        if (!((bitField0_ & 0x00000001) == 0x00000001)) {
          bio_ = new java.util.ArrayList<Bio.BioData>(bio_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilder<
          Bio.BioData, Bio.BioData.Builder, Bio.BioDataOrBuilder> bioBuilder_;

      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public java.util.List<Bio.BioData> getBioList() {
        if (bioBuilder_ == null) {
          return java.util.Collections.unmodifiableList(bio_);
        } else {
          return bioBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public int getBioCount() {
        if (bioBuilder_ == null) {
          return bio_.size();
        } else {
          return bioBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Bio.BioData getBio(int index) {
        if (bioBuilder_ == null) {
          return bio_.get(index);
        } else {
          return bioBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder setBio(
          int index, Bio.BioData value) {
        if (bioBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBioIsMutable();
          bio_.set(index, value);
          onChanged();
        } else {
          bioBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder setBio(
          int index, Bio.BioData.Builder builderForValue) {
        if (bioBuilder_ == null) {
          ensureBioIsMutable();
          bio_.set(index, builderForValue.build());
          onChanged();
        } else {
          bioBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder addBio(Bio.BioData value) {
        if (bioBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBioIsMutable();
          bio_.add(value);
          onChanged();
        } else {
          bioBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder addBio(
          int index, Bio.BioData value) {
        if (bioBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureBioIsMutable();
          bio_.add(index, value);
          onChanged();
        } else {
          bioBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder addBio(
          Bio.BioData.Builder builderForValue) {
        if (bioBuilder_ == null) {
          ensureBioIsMutable();
          bio_.add(builderForValue.build());
          onChanged();
        } else {
          bioBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder addBio(
          int index, Bio.BioData.Builder builderForValue) {
        if (bioBuilder_ == null) {
          ensureBioIsMutable();
          bio_.add(index, builderForValue.build());
          onChanged();
        } else {
          bioBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder addAllBio(
          Iterable<? extends Bio.BioData> values) {
        if (bioBuilder_ == null) {
          ensureBioIsMutable();
          super.addAll(values, bio_);
          onChanged();
        } else {
          bioBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder clearBio() {
        if (bioBuilder_ == null) {
          bio_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          bioBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Builder removeBio(int index) {
        if (bioBuilder_ == null) {
          ensureBioIsMutable();
          bio_.remove(index);
          onChanged();
        } else {
          bioBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Bio.BioData.Builder getBioBuilder(
          int index) {
        return getBioFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Bio.BioDataOrBuilder getBioOrBuilder(
          int index) {
        if (bioBuilder_ == null) {
          return bio_.get(index);  } else {
          return bioBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public java.util.List<? extends Bio.BioDataOrBuilder>
           getBioOrBuilderList() {
        if (bioBuilder_ != null) {
          return bioBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(bio_);
        }
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Bio.BioData.Builder addBioBuilder() {
        return getBioFieldBuilder().addBuilder(
            Bio.BioData.getDefaultInstance());
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public Bio.BioData.Builder addBioBuilder(
          int index) {
        return getBioFieldBuilder().addBuilder(
            index, Bio.BioData.getDefaultInstance());
      }
      /**
       * <code>repeated .boa.bio.types.BioData bio = 1;</code>
       */
      public java.util.List<Bio.BioData.Builder>
           getBioBuilderList() {
        return getBioFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilder<
          Bio.BioData, Bio.BioData.Builder, Bio.BioDataOrBuilder>
          getBioFieldBuilder() {
        if (bioBuilder_ == null) {
          bioBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<
              Bio.BioData, Bio.BioData.Builder, Bio.BioDataOrBuilder>(
                  bio_,
                  ((bitField0_ & 0x00000001) == 0x00000001),
                  getParentForChildren(),
                  isClean());
          bio_ = null;
        }
        return bioBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:boa.bio.types.BiologyDataset)
    }

    static {
      defaultInstance = new BiologyDataset(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:boa.bio.types.BiologyDataset)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_boa_bio_types_BiologyDataset_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_boa_bio_types_BiologyDataset_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\rBiology.proto\022\rboa.bio.types\032\tBio.prot" +
      "o\"5\n\016BiologyDataset\022#\n\003bio\030\001 \003(\0132\026.boa.b" +
      "io.types.BioData"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_boa_bio_types_BiologyDataset_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_boa_bio_types_BiologyDataset_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_boa_bio_types_BiologyDataset_descriptor,
              new String[] { "Bio", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          Bio.getDescriptor(),
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
