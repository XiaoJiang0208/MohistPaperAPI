package com.mohistmc.feature;

import com.mohistmc.util.I18n;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Why is there such a class?
 * Because we have included some MOD optimizations and modifications,
 * as well as some mods that are only used on the client, these cannot be loaded in Mohist
 */
public class AutoDeleteMods {
    public static final List<String> classlist = new ArrayList<>(Arrays.asList(
            "org.spongepowered.mod.SpongeMod" /*SpongeForge*/,
            "me.wesley1808.servercore.common.ServerCore" /*ServerCore*/,
            "i18nupdatemod.I18nUpdateMod" /*I18nUpdateMod*/,
            "optifine.Differ" /*OptiFine*/));

    public static void jar() throws Exception {
        System.out.println(I18n.as("update.mods"));
        for (String t : classlist) {
            check(t);
        }
    }

    public static void check(String content) throws Exception {
        String cl = content.split("\\|")[0].replaceAll("\\.", "/") + ".class";
        File mods = new File("mods");
        if (!mods.exists()) mods.mkdir();
        for (File f : mods.listFiles((dir, name) -> name.endsWith(".jar"))) {
            if (fileExists(f, cl)) {
                System.out.println(I18n.as("update.deleting", f.getName()));
                System.gc();
                Thread.sleep(100);
                File newf = new File("delete/mods");
                File qnewf = new File("delete", f.getPath());
                if (!newf.exists()) {
                    newf.mkdirs();
                } else {
                    if (qnewf.exists()) qnewf.delete();
                }
                Files.copy(f.toPath(), qnewf.toPath(), StandardCopyOption.REPLACE_EXISTING);
                f.delete();
            }
        }
    }


    private static boolean fileExists(File f, String fName) {
        if (!f.exists()) return false;
        try {
            JarFile jf = new JarFile(f);
            if (jf.getJarEntry(fName) != null) {
                jf.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println("[Mohist | ALERT] - The jar file " + f.getName() + " (at " + f.getAbsolutePath() + ") is maybe corrupted or empty.");
            return false;
        }
        return false;
    }
}
