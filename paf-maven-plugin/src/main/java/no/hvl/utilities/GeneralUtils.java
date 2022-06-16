package no.hvl.utilities;

import com.github.javaparser.ast.body.BodyDeclaration;
import no.hvl.exceptions.ExerciseIdException;
import no.hvl.exceptions.NodeException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static no.hvl.utilities.AnnotationUtils.*;

public class GeneralUtils {

    private GeneralUtils(){
        throw new IllegalStateException("This is an utility class. It is not meant to be instantiated");
    }

    public static void sortNodesAnnotatedWithExerciseByIdAsc(List<BodyDeclaration<?>> nodesAnnotatedWithExercise){
        nodesAnnotatedWithExercise.sort((node1, node2) -> {
            int[] node1Id = getIdValueInExerciseAnnotation(node1);
            int[] node2Id = getIdValueInExerciseAnnotation(node2);
            return compareIntegerArrays(node1Id, node2Id);
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

    public static void checkExerciseIds(List<BodyDeclaration<?>> nodes){
        Set<List<Integer>> legalIds = new HashSet<>();
        for(BodyDeclaration<?> node: nodes){
            int[] id = getIdValueInExerciseAnnotation(node);
            try{
                checkIfExerciseIdIsLegal(legalIds, id);
            }catch (ExerciseIdException e){
                throw new NodeException(node, e.getMessage());
            }
        }
    }

    public static void checkIfExerciseIdIsLegal(Set<List<Integer>> legalIds, int[] id){
        List<Integer> digitsInId = new ArrayList<>();
        for (int digit : id) {
            digitsInId.add(digit);
            throwExceptionIfZeroBased(digitsInId);
            throwExceptionIfMissingRequiredId(digitsInId, legalIds);
            legalIds.add(new ArrayList<>(digitsInId));
        }
    }

    private static void throwExceptionIfZeroBased(List<Integer> digitsInId) {
        int lastDigit = digitsInId.get(digitsInId.size()-1);
        if(lastDigit == 0){
            throw new ExerciseIdException(digitsInId);
        }
    }

    private static void throwExceptionIfMissingRequiredId(List<Integer> digitsInId,
                                                          Set<List<Integer>> legalIds) {
        int lastDigit = digitsInId.get(digitsInId.size()-1);
        List<Integer> requiredId = getRequiredId(digitsInId);
        if(lastDigit != 1 && !legalIds.contains(requiredId)){
            throw new ExerciseIdException(digitsInId, requiredId);
        }
    }

    private static List<Integer> getRequiredId(List<Integer> digitsInId) {
        List<Integer> requiredId = new ArrayList<>(digitsInId);
        int lastIndex = requiredId.size()-1;
        requiredId.set(lastIndex, requiredId.get(lastIndex)-1);
        return requiredId;
    }

    public static String removeDescriptionAttributes(String description){
        return description.lines()
                .dropWhile(Predicate.not(String::isBlank))
                .collect(Collectors.joining("\n", "", "\n"));
    }

}
