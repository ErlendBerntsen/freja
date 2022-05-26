package no.hvl.utilities;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.concepts.Exercise;
import no.hvl.exceptions.ExerciseNumberException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void checkExerciseNumbers(List<BodyDeclaration<?>> nodes){
        List<int[]> exerciseNumbers = nodes
                .stream()
                .map(AnnotationUtils::getNumberValueInImplementAnnotation)
                .collect(Collectors.toList());
        checkIfExerciseNumbersAreLegal(exerciseNumbers);
    }

    public static void checkIfExerciseNumbersAreLegal(List<int[]> exerciseNumbers){
        HashSet<List<Integer>> legalNumbers = new HashSet<>();
        for(int[] number : exerciseNumbers){
            List<Integer> digitsInNumber = new ArrayList<>();
            for (int digit : number) {
                digitsInNumber.add(digit);
                throwExceptionIfZeroBased(digitsInNumber);
                throwExceptionIfMissingRequiredNumber(digitsInNumber, legalNumbers);
                legalNumbers.add(new ArrayList<>(digitsInNumber));
            }
        }
    }

    private static void throwExceptionIfZeroBased(List<Integer> digitsInNumber) {
        int lastDigit = digitsInNumber.get(digitsInNumber.size()-1);
        if(lastDigit == 0){
            throw new ExerciseNumberException(digitsInNumber);
        }
    }

    private static void throwExceptionIfMissingRequiredNumber(List<Integer> digitsInNumber,
                                                              HashSet<List<Integer>> legalNumbers) {
        int lastDigit = digitsInNumber.get(digitsInNumber.size()-1);
        List<Integer> requiredNumber = getRequiredNumber(digitsInNumber);
        if(lastDigit != 1 && !legalNumbers.contains(requiredNumber)){
            throw new ExerciseNumberException(digitsInNumber, requiredNumber);
        }
    }

    private static List<Integer> getRequiredNumber(List<Integer> digitsInNumber) {
        List<Integer> requiredNumber = new ArrayList<>(digitsInNumber);
        int lastIndex = requiredNumber.size()-1;
        requiredNumber.set(lastIndex, requiredNumber.get(lastIndex)-1);
        return requiredNumber;
    }

}
