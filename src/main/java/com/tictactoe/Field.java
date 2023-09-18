package com.tictactoe;

import java.util.*;
import java.util.stream.Collectors;

public class Field {
    private final Map<Integer, Sign> field;
    private static List<List<Integer>> winOptions = new ArrayList<>();

    static {
        winOptions.add(Arrays.asList(0, 1, 2));
        winOptions.add(Arrays.asList(3, 4, 5));
        winOptions.add(Arrays.asList(6, 7, 8));
        winOptions.add(Arrays.asList(0, 3, 6));
        winOptions.add(Arrays.asList(1, 4, 7));
        winOptions.add(Arrays.asList(2, 5, 8));
        winOptions.add(Arrays.asList(0, 4, 8));
        winOptions.add(Arrays.asList(2, 4, 6));

    }

    public Field() {
        field = new HashMap<>();
        field.put(0, Sign.EMPTY);
        field.put(1, Sign.EMPTY);
        field.put(2, Sign.EMPTY);
        field.put(3, Sign.EMPTY);
        field.put(4, Sign.EMPTY);
        field.put(5, Sign.EMPTY);
        field.put(6, Sign.EMPTY);
        field.put(7, Sign.EMPTY);
        field.put(8, Sign.EMPTY);
    }

    public Map<Integer, Sign> getField() {
        return field;
    }

    public int getEmptyFieldIndex() {
        return field.entrySet().stream()
                .filter(e -> e.getValue() == Sign.EMPTY)
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);
    }
    public List<Sign> getFieldData() {
        return field.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    public long getEmptyCellsCount() {
        return field.entrySet().stream()
                .filter(e -> e.getValue() == Sign.EMPTY)
                .count();
    }

    public Sign checkWin() {
        List<List<Integer>> winPossibilities = List.of(
                List.of(0, 1, 2),
                List.of(3, 4, 5),
                List.of(6, 7, 8),
                List.of(0, 3, 6),
                List.of(1, 4, 7),
                List.of(2, 5, 8),
                List.of(0, 4, 8),
                List.of(2, 4, 6)
        );
        for (List<Integer> winPossibility : winPossibilities) {
            if (field.get(winPossibility.get(0)) == field.get(winPossibility.get(1))
                    && field.get(winPossibility.get(0)) == field.get(winPossibility.get(2))
                    && field.get(winPossibility.get(0)) != Sign.EMPTY) {
                return field.get(winPossibility.get(0));
            }
        }
        return Sign.EMPTY;
    }

    public int calculationOfWinningMoves(int cross) {
        int result = checkWinOfNought();
        if (result != -1) return result;
        else if ((result = calculationIfTwoCellWithCross(cross)) != -1) return result;
        else if (getEmptyCellsCount() == 8 && cross != 4 && field.get(4) == Sign.EMPTY) return 4;
        else if (getEmptyCellsCount() == 8 && cross == 4 && field.get(4) == Sign.CROSS) return 0;
        else if ((result = calculationIfOneCellWithCross()) != -1) return result;
        else return getEmptyFieldIndex();

    }

    private int calculationIfOneCellWithCross() {
        // Если две ячейки пустые, а одна с крестиком
        for (List<Integer> list : winOptions) {
            if ( (field.get(list.get(0)) == Sign.EMPTY && field.get(list.get(1)) == Sign.EMPTY
                    || field.get(list.get(1)) == Sign.EMPTY && field.get(list.get(2)) == Sign.EMPTY
                    || field.get(list.get(0)) == Sign.EMPTY && field.get(list.get(2)) == Sign.EMPTY)
                    &&
                    (field.get(list.get(0)) == Sign.CROSS || field.get(list.get(1)) == Sign.CROSS
                            || field.get(list.get(2)) == Sign.CROSS) ) {
                List<Integer> empties = list.stream().filter(x -> field.get(x) == Sign.EMPTY).toList();
                return empties.get(1);
            }
        }
        return -1;
    }
    private int calculationIfTwoCellWithCross(int cross) {
        // Если две ячейки с крестиком, а одна пустая
        for (List<Integer> list : winOptions) {
            if (list.contains(cross)) {
                if ( (field.get(list.get(0)) == Sign.CROSS && field.get(list.get(1)) == Sign.CROSS
                        || field.get(list.get(1)) == Sign.CROSS && field.get(list.get(2)) == Sign.CROSS
                        || field.get(list.get(0)) == Sign.CROSS && field.get(list.get(2)) == Sign.CROSS)
                        && (field.get(list.get(0)) == Sign.EMPTY || field.get(list.get(1)) == Sign.EMPTY ||
                        field.get(list.get(2)) == Sign.EMPTY) ) {
                    return list.stream().filter(x -> field.get(x) == Sign.EMPTY).findFirst().orElse(getEmptyFieldIndex());
                }
            }
        }
        return -1;
    }
    private int checkWinOfNought() {
        // Проверка на победу ноликов
        for (List<Integer> list : winOptions) {
            if ((field.get(list.get(0)) == Sign.NOUGHT && field.get(list.get(1)) == Sign.NOUGHT
                    || field.get(list.get(1)) == Sign.NOUGHT && field.get(list.get(2)) == Sign.NOUGHT
                    || field.get(list.get(0)) == Sign.NOUGHT && field.get(list.get(2)) == Sign.NOUGHT)
                    && (field.get(list.get(0)) == Sign.EMPTY || field.get(list.get(1)) == Sign.EMPTY ||
                    field.get(list.get(2)) == Sign.EMPTY)) {
                return list.stream().filter(x -> field.get(x) == Sign.EMPTY).findFirst().orElse(getEmptyFieldIndex());
            }
        }
        return -1;
    }
}