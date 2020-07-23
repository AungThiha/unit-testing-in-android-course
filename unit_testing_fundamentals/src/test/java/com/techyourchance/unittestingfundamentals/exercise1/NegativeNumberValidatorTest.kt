package com.techyourchance.unittestingfundamentals.exercise1

import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NegativeNumberValidatorTest {

  private lateinit var validator: NegativeNumberValidator

  @Before
  fun setup() {
    validator = NegativeNumberValidator()
  }

  @Test
  fun isNegative_Positive_False() {
    val result = validator.isNegative(1)
    Assert.assertThat(result, `is`(false))
  }

  @Test
  fun isNegative_Zero_False() {
    val result = validator.isNegative(0)
    Assert.assertThat(result, `is`(false))
  }

  @Test
  fun isNegative_Negative_True() {
    val result = validator.isNegative(-1)
    Assert.assertThat(result, `is`(true))
  }

}