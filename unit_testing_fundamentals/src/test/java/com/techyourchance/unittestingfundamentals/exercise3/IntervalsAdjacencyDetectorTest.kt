package com.techyourchance.unittestingfundamentals.exercise3

import com.techyourchance.unittestingfundamentals.example3.Interval
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IntervalsAdjacencyDetectorTest {

  private lateinit var detector: IntervalsAdjacencyDetector

  @Before
  fun setup() {
    detector = IntervalsAdjacencyDetector()
  }

  // interval1 is before interval 2
  @Test
  fun isAdjacent_interval1BeforeInterval2_false() {
    assertTestFalse(1, 2, 4, 6)
    assertTestFalse(-10, -5, -3, -2)
    assertTestFalse(-10, -5, -3, 5)
    assertTestFalse(-10, -5, 1, 5)
  }

  // interval1 adjacent interval 2 on the left
  @Test
  fun isAdjacent_interval1AdjacentInterval2_true() {
    assertTestTrue(1, 2, 2, 6)
    assertTestTrue(-10, -5, -5, -2)
    assertTestTrue(-10, -3, -3, 5)
    assertTestTrue(-10, 1, 1, 5)
  }

  // interval1 is in interval 2 on the left edge
  @Test
  fun isAdjacent_interval1InInterval2OnLeftEdge_false() {
    assertTestFalse(1, 2, 1, 6)
    assertTestFalse(-10, -5, -10, -2)
    assertTestFalse(-5, -3, -5, 5)
    assertTestFalse(-5, 2, -5, 5)
  }

  // interval1 is in interval 2 without touching edges
  @Test
  fun isAdjacent_interval1InInterval2_false() {
    assertTestFalse(1, 2, -1, 6)
    assertTestFalse(-10, -5, -11, -2)
    assertTestFalse(-5, -3, -10, 5)
    assertTestFalse(-2, 2, -10, 10)
    assertTestFalse(5, 6, 1, 10)
  }

  // interval1 and interval 2 are the same
  @Test
  fun isAdjacent_interval1SameInterval2_false() {
    assertTestFalse(1, 2, 1, 2)
    assertTestFalse(-10, -5, -10, -5)
    assertTestFalse(-5, -3, -5, -3)
    assertTestFalse(-2, 2, -10, 10)
  }

  // interval1 is in interval 2 on the right edge
  @Test
  fun isAdjacent_interval1InInterval2OnRightEdge_false() {
    assertTestFalse(4, 5, 1, 5)
    assertTestFalse(-8, -5, -10, -5)
    assertTestFalse(-5, 1, -10, 1)
  }

  // interval1 adjacent interval 2 on the right
  @Test
  fun isAdjacent_interval1AdjacentInterval2OnRight_true() {
    assertTestTrue(2, 6, 1, 2)
    assertTestTrue(-5, -2, -10, -5)
    assertTestTrue(-3, 5, -10, -3)
    assertTestTrue(1, 5, -10, 1)
  }

  // interval1 is after interval 2
  @Test
  fun isAdjacent_interval1AfterInterval2_false() {
    assertTestFalse(4, 6, 1, 2)
    assertTestFalse( -3, -2, -10, -5)
    assertTestFalse(-3, 5, -10, -5)
    assertTestFalse(1, 5, -10, -5)
  }

  // interval1 contains interval 2 on the left edge
  @Test
  fun isAdjacent_interval1ContainsInterval2OnItsLeftEdge_false() {
    assertTestFalse(1, 6, 1, 2)
    assertTestFalse(-10, -2, -10, -5)
    assertTestFalse(-5, 5, -5, -3)
    assertTestFalse(-5, 5, -5, 2)
  }

  // interval1 contains interval 2 on the right edge
  @Test
  fun isAdjacent_interval1ContainsInterval2OnItsRightEdge_false() {
    assertTestFalse(1, 5, 4, 5)
    assertTestFalse(-10, -5, -8, -5)
    assertTestFalse(-10, 1, -5, 1)
  }

  // interval 1 contains interval 2 without touching edges
  @Test
  fun isAdjacent_interval1ContainsInterval2_false() {
    assertTestFalse(-1, 6, 1, 2)
    assertTestFalse(-11, -2, -10, -5)
    assertTestFalse(-10, 5, -5, -3)
    assertTestFalse(-10, 10, -2, 2)
    assertTestFalse(1, 10, 5, 6)
  }

  private fun assertTestTrue(start1: Int, end1: Int, start2: Int, end2: Int) {
    val result = getResult(start1, end1, start2, end2)
    assertTrue(result)
  }

  private fun assertTestFalse(start1: Int, end1: Int, start2: Int, end2: Int) {
    val result = getResult(start1, end1, start2, end2)
    assertFalse(result)
  }

  private fun getResult(start1: Int, end1: Int, start2: Int, end2: Int): Boolean {
    val interval1 = Interval(start1, end1)
    val interval2 = Interval(start2, end2)
    return detector.isAdjacent(interval1, interval2)
  }


}