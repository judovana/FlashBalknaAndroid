package dalvik.system;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.PluginFactoryProvider;
import org.fbb.balkna.model.utils.JavaPluginProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by jvanek on 12/7/15.
 */
public class DalvikPluginProvider implements PluginFactoryProvider {
    @Override
    public void addResource(URL u) throws Exception {
        //java
        if (ClassLoader.getSystemClassLoader() instanceof URLClassLoader) {
            new JavaPluginProvider().addResource(u);
//            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//            Class sysclass = URLClassLoader.class;
//
//            Method method = sysclass.getDeclaredMethod("addURL", parameters);
//
//            method.setAccessible(
//                    true);
//            method.invoke(sysloader,
//                    new Object[]{u});
        } else {
            //android and its http://developer.android.com/reference/dalvik/system/PathClassLoader.html
            //where one must add item to
            //structured lists of path elements */
            //private final DexPathList pathList;
            if (!u.getProtocol().toLowerCase().startsWith("file")){
                File orig = new File(u.getFile());
                String origName = orig.getName();
                File savedAs = File.createTempFile(origName, origName);
                Model.saveUrl(u, savedAs);
                u = savedAs.toURI().toURL();
            }
            File toAppend = new File(u.getFile());

            PathClassLoader origpc =  (PathClassLoader)ClassLoader.getSystemClassLoader();
            PathClassLoader  nwpc = new PathClassLoader(toAppend.getAbsolutePath(), origpc.getParent());


            Class dcClass1 = origpc.getClass().getSuperclass();
            Class dcClass2 = nwpc.getClass().getSuperclass();

            Field pathList1 = dcClass1.getDeclaredField("pathList");
            Field pathList2 = dcClass2.getDeclaredField("pathList");
            pathList1.setAccessible(true);
            pathList2.setAccessible(true);
            //dalvik.system.DexPathList
            Object oldPaths = pathList1.get(origpc);
            Object newPaths = pathList2.get(nwpc);

            //private final Element[] dexElements;
            Field dexElements1 = oldPaths.getClass().getDeclaredField("dexElements");
            Field dexElements2 = newPaths.getClass().getDeclaredField("dexElements");
            dexElements1.setAccessible(true);
            dexElements2.setAccessible(true);
            Object[] oldElements = (Object[]) dexElements1.get(oldPaths);
            Object[] newElements = (Object[]) dexElements2.get(newPaths);

            //Class cl = Class.forName("[Ldalvik.system.DexPathList$Element;");
            Class cl = Class.forName("dalvik.system.DexPathList$Element");
            Object adapted = Array.newInstance(cl, oldElements.length + newElements.length);
            for (int x = 0; x < oldElements.length; x++){
                Array.set(adapted,x, oldElements[x]);
            }
            for (int x = 0; x < newElements.length; x++){
                Array.set(adapted,x+oldElements.length, newElements[x]);
            }
            dexElements1.set(oldPaths, adapted);

            Field test = oldPaths.getClass().getDeclaredField("dexElements");
            test.setAccessible(true);
            Object[] testElements = (Object[]) test.get(oldPaths);
            System.out.println(testElements.length);


        }
    }


}
