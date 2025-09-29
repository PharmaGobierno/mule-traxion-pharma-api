package com.traxion.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.53.0)",
    comments = "Source: RecursoConfiable.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RecursoConfiableRPCGrpc {

  private RecursoConfiableRPCGrpc() {}

  public static final String SERVICE_NAME = "RecursoConfiableRPC";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<RecursoConfiable.RegisterRequest,
      RecursoConfiable.RegisterReply> getRegisterPlateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterPlate",
      requestType = RecursoConfiable.RegisterRequest.class,
      responseType = RecursoConfiable.RegisterReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<RecursoConfiable.RegisterRequest,
      RecursoConfiable.RegisterReply> getRegisterPlateMethod() {
    io.grpc.MethodDescriptor<RecursoConfiable.RegisterRequest, RecursoConfiable.RegisterReply> getRegisterPlateMethod;
    if ((getRegisterPlateMethod = RecursoConfiableRPCGrpc.getRegisterPlateMethod) == null) {
      synchronized (RecursoConfiableRPCGrpc.class) {
        if ((getRegisterPlateMethod = RecursoConfiableRPCGrpc.getRegisterPlateMethod) == null) {
          RecursoConfiableRPCGrpc.getRegisterPlateMethod = getRegisterPlateMethod =
              io.grpc.MethodDescriptor.<RecursoConfiable.RegisterRequest, RecursoConfiable.RegisterReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterPlate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RecursoConfiable.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RecursoConfiable.RegisterReply.getDefaultInstance()))
              .setSchemaDescriptor(new RecursoConfiableRPCMethodDescriptorSupplier("RegisterPlate"))
              .build();
        }
      }
    }
    return getRegisterPlateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RecursoConfiableRPCStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RecursoConfiableRPCStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RecursoConfiableRPCStub>() {
        @java.lang.Override
        public RecursoConfiableRPCStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RecursoConfiableRPCStub(channel, callOptions);
        }
      };
    return RecursoConfiableRPCStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RecursoConfiableRPCBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RecursoConfiableRPCBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RecursoConfiableRPCBlockingStub>() {
        @java.lang.Override
        public RecursoConfiableRPCBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RecursoConfiableRPCBlockingStub(channel, callOptions);
        }
      };
    return RecursoConfiableRPCBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RecursoConfiableRPCFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RecursoConfiableRPCFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RecursoConfiableRPCFutureStub>() {
        @java.lang.Override
        public RecursoConfiableRPCFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RecursoConfiableRPCFutureStub(channel, callOptions);
        }
      };
    return RecursoConfiableRPCFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class RecursoConfiableRPCImplBase implements io.grpc.BindableService {

    /**
     */
    public void registerPlate(RecursoConfiable.RegisterRequest request,
        io.grpc.stub.StreamObserver<RecursoConfiable.RegisterReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterPlateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterPlateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                RecursoConfiable.RegisterRequest,
                RecursoConfiable.RegisterReply>(
                  this, METHODID_REGISTER_PLATE)))
          .build();
    }
  }

  /**
   */
  public static final class RecursoConfiableRPCStub extends io.grpc.stub.AbstractAsyncStub<RecursoConfiableRPCStub> {
    private RecursoConfiableRPCStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RecursoConfiableRPCStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RecursoConfiableRPCStub(channel, callOptions);
    }

    /**
     */
    public void registerPlate(RecursoConfiable.RegisterRequest request,
        io.grpc.stub.StreamObserver<RecursoConfiable.RegisterReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterPlateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RecursoConfiableRPCBlockingStub extends io.grpc.stub.AbstractBlockingStub<RecursoConfiableRPCBlockingStub> {
    private RecursoConfiableRPCBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RecursoConfiableRPCBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RecursoConfiableRPCBlockingStub(channel, callOptions);
    }

    /**
     */
    public RecursoConfiable.RegisterReply registerPlate(RecursoConfiable.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterPlateMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RecursoConfiableRPCFutureStub extends io.grpc.stub.AbstractFutureStub<RecursoConfiableRPCFutureStub> {
    private RecursoConfiableRPCFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RecursoConfiableRPCFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RecursoConfiableRPCFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<RecursoConfiable.RegisterReply> registerPlate(
        RecursoConfiable.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterPlateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_PLATE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RecursoConfiableRPCImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RecursoConfiableRPCImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_PLATE:
          serviceImpl.registerPlate((RecursoConfiable.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<RecursoConfiable.RegisterReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RecursoConfiableRPCBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RecursoConfiableRPCBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return RecursoConfiable.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RecursoConfiableRPC");
    }
  }

  private static final class RecursoConfiableRPCFileDescriptorSupplier
      extends RecursoConfiableRPCBaseDescriptorSupplier {
    RecursoConfiableRPCFileDescriptorSupplier() {}
  }

  private static final class RecursoConfiableRPCMethodDescriptorSupplier
      extends RecursoConfiableRPCBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RecursoConfiableRPCMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RecursoConfiableRPCGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RecursoConfiableRPCFileDescriptorSupplier())
              .addMethod(getRegisterPlateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
