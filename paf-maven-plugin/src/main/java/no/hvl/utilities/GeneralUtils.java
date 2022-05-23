package no.hvl.utilities;

import com.github.javaparser.ast.body.BodyDeclaration;

import java.util.List;

import static no.hvl.utilities.AnnotationUtils.*;

public class GeneralUtils {

    private GeneralUtils(){
        throw new IllegalStateException("This is an utility class. It is not meant to be instantiated");
    }

    public static void sortNodesAnnotatedWithImplementByNumberAsc(List<BodyDeclaration<?>> nodesAnnotatedWithImplement){
        nodesAnnotatedWithImplement.sort((node1, node2) -> {
            int[] node1Number = getNumberValueInImplementAnnotation(node1);
            int[] node2Number = getNumberValueInImplementAnnotation(node2);
            return compareIntegerArrays(node1Number, node2Number);
        });
    }

    public static int compareIntegerArrays(int[] array1, int[] array2){
        for(int i = 0; i < array1.length; i++){
            if(i >= array2.length){
                return 1;
            }
            int comparison = Integer.compare(array1[i], array2[i]);
            if(comparison != 0){
                return comparison;
            }
        }
        if(array1.length == array2.length){
            return 0;
        }
        return -1;
    }

}
