import sun.hotspot.WhiteBox;
import sun.jvm.hotspot.utilities.SystemDictionaryHelper;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.HotSpotAgent;

/*
1. compile Read.java and run it
~/tests$ ../openjdk/jdk_binaries/bin/javac Read.java
~/tests$ ../openjdk/jdk_binaries/bin/java Read

2. open another terminal and compile this code
~/tests$ ../openjdk/jdk_binaries/bin/javac \
 -cp ../jdk_gold/build/jdk/modules/jdk.hotspot.agent:. \
 ClassSize.java

3. find the PID of Read
$ ps -efa | grep java
...   433169  ... ../openjdk/jdk_binaries/bin/java Read

4. run this passing the PID
~/tests$ ../openjdk/jdk_binaries/bin/java \
 --add-modules=jdk.hotspot.agent \
 --add-exports=jdk.hotspot.agent/sun.jvm.hotspot=ALL-UNNAMED \
 --add-exports=jdk.hotspot.agent/sun.jvm.hotspot.utilities=ALL-UNNAMED \
 --add-exports=jdk.hotspot.agent/sun.jvm.hotspot.oops=ALL-UNNAMED \
 --add-exports=jdk.hotspot.agent/sun.jvm.hotspot.debugger=ALL-UNNAMED \
 -Xbootclasspath/a:.:../jdk_gold/build/jdk/modules/jdk.hotspot.agent \
 -XX:+UnlockDiagnosticVMOptions \
 -XX:+WhiteBoxAPI \
 ClassSize 433169
Size: 536
pid: 433169
Size: 544
*/

class ClassSize
{
    public static void main(String... args)
    {
        WhiteBox wb = WhiteBox.getWhiteBox();
        try {
            Class<?> iklass = Class.forName("java.lang.Object");
            long size = wb.getKlassMetadataSize(iklass);
            System.out.println("Size: " + size);
        }
        catch (Exception ex) {
            System.out.println("ouch!");
        }

        System.out.println("pid: " + args[0]);
        int pid = 0;
        try {
            pid = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.out.println("pid error");
            return;
        }

        HotSpotAgent agent = new HotSpotAgent();
        agent.attach(pid);
        InstanceKlass ik = SystemDictionaryHelper.findInstanceKlass("java.lang.Object");
        long size = ik.getSize();
        System.out.println("Size: " + size);
        agent.detach();
    }
}
