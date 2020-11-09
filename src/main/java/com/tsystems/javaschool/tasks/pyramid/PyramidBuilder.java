package com.tsystems.javaschool.tasks.pyramid;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PyramidBuilder {

  /**
   * Builds a pyramid with sorted values (with minumum value at the top line and maximum at the
   * bottom, from left to right). All vacant positions in the array are zeros.
   *
   * @param inputNumbers to be used in the pyramid
   * @return 2d array with pyramid inside
   * @throws {@link CannotBuildPyramidException} if the pyramid cannot be build with given input
   */
  public int[][] buildPyramid(List<Integer> inputNumbers) {
    try {
      inputNumbers.sort(Comparator.naturalOrder());
    } catch (OutOfMemoryError | NullPointerException ex) {
      throw new CannotBuildPyramidException();
    }

    int height = getHeight(inputNumbers.size());
    int width = 2 * height - 1;
    int middleIndex = height - 1;

    int[][] result = new int[height][width];
    Iterator<Integer> digits = inputNumbers.iterator();

    for (int row = 0; row <= middleIndex; row++) {
      int digitsInThisRow = row + 1;              //<- every next row will have count+1 of digits
      int indexOfFirstInRow = middleIndex - row;  //<- index of first digit in a row to inject

      if (digits.hasNext() && digitsInThisRow != 0) {
        for (int i = 0; i < digitsInThisRow * 2; i = i + 2) { // d1 -> 0 -> d2 -> 0 -> d3 ...
          result[row][indexOfFirstInRow + i] = digits.next();
        }
      }
    }
    return result;
  }

  private int getHeight(int size) {
    int height = 0;       //<- is flat :)
    while (size > 0) {    //<- new stages of pyramid need new blocks
      size -= ++height;   //<- first we will increase the height and reduce our blocks count
    }

    if (size == 0) {      //<- so, after calculating,
      return height;      //   blocksCount must to be 0 to build good pyramid
    }
    throw new CannotBuildPyramidException();
  }
}
