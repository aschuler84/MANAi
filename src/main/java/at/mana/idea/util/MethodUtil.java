package at.mana.idea.util;

import at.mana.core.util.HashUtil;
import at.mana.core.util.KeyValuePair;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.ClassUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodUtil {

    public static List<KeyValuePair<PsiMethod, String>> buildHashFromMethods(PsiJavaFile file ) {
        PsiClass[] classes = file.getClasses();
         return Arrays.stream(classes).flatMap(c -> Arrays.stream(c.getMethods()))
                .map(m -> new KeyValuePair<>(m, HashUtil.hash(
                        m.getName(),
                        ClassUtil.getJVMClassName(Objects.requireNonNull(m.getContainingClass())),
                        ClassUtil.getAsmMethodSignature(m))))
                .collect(Collectors.toList());
    }

}
