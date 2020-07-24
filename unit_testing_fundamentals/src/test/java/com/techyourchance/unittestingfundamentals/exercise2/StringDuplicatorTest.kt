package com.techyourchance.unittestingfundamentals.exercise2

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StringDuplicatorTest {

  private lateinit var duplicator: StringDuplicator

  @Before
  fun setup() {
    duplicator = StringDuplicator()
  }

  @Test
  fun duplicate_empty_noDuplication() {
    val result = duplicator.duplicate("")
    assertEquals("", result)
  }

  @Test
  fun duplicate_whiteSpace_duplicated() {
    val result = duplicator.duplicate("  ")
    assertEquals("    ", result)

    val result2 = duplicator.duplicate("    ")
    assertEquals("        ", result2)

    val result3 = duplicator.duplicate("    \n   \n ")
    assertEquals("    \n   \n     \n   \n ", result3)
  }

  @Test
  fun duplicate_chars_duplicated() {
    val result = duplicator.duplicate("dafd adfd ")
    assertEquals("dafd adfd dafd adfd ", result)

    val result2 = duplicator.duplicate("dafldjf &(*& @#$#@  \n afdadf/")
    assertEquals("dafldjf &(*& @#$#@  \n afdadf/dafldjf &(*& @#$#@  \n afdadf/", result2)
  }

}