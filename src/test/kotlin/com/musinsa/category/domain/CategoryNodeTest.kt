package com.musinsa.category.domain

import com.musinsa.category.entity.Category
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class CategoryNodeTest {

    @Test
    fun `노드 생성 테스트`() {
        // Given
        val category = Category.create("의류", null, 0, 1).copy(id = 1L)

        // When
        val node = CategoryNode(category)

        // Then
        assertEquals(category, node.category)
        assertNull(node.parent)
        assertTrue(node.children.isEmpty())
        assertTrue(node.isRoot())
        assertTrue(node.isLeaf())
    }

    @Test
    fun `자식 노드 추가 테스트`() {
        // Given
        val parentCategory = Category.create("의류", null, 0, 1).copy(id = 1L)
        val childCategory = Category.create("상의", 1L, 1, 1).copy(id = 2L)
        val parentNode = CategoryNode(parentCategory)
        val childNode = CategoryNode(childCategory)

        // When
        parentNode.addChild(childNode)

        // Then
        assertEquals(1, parentNode.children.size)
        assertEquals(childNode, parentNode.children[0])
        assertEquals(parentNode, childNode.parent)
        assertFalse(parentNode.isLeaf())
        assertFalse(childNode.isRoot())
    }

    @Test
    fun `자식 노드 제거 테스트`() {
        // Given
        val parentCategory = Category.create("의류", null, 0, 1).copy(id = 1L)
        val childCategory = Category.create("상의", 1L, 1, 1).copy(id = 2L)
        val parentNode = CategoryNode(parentCategory)
        val childNode = CategoryNode(childCategory)
        parentNode.addChild(childNode)

        // When
        parentNode.removeChild(childNode)

        // Then
        assertTrue(parentNode.children.isEmpty())
        assertNull(childNode.parent)
        assertTrue(parentNode.isLeaf())
        assertTrue(childNode.isRoot())
    }

    @Test
    fun `중복 자식 추가 방지 테스트`() {
        // Given
        val parentCategory = Category.create("의류", null, 0, 1).copy(id = 1L)
        val childCategory = Category.create("상의", 1L, 1, 1).copy(id = 2L)
        val parentNode = CategoryNode(parentCategory)
        val childNode = CategoryNode(childCategory)

        // When
        parentNode.addChild(childNode)
        parentNode.addChild(childNode) // 중복 추가

        // Then
        assertEquals(1, parentNode.children.size) // 여전히 1개만 있어야 함
    }

    @Test
    fun `자식 노드 정렬 테스트`() {
        // Given
        val parentCategory = Category.create("의류", null, 0, 1).copy(id = 1L)
        val child1 = CategoryNode(Category.create("하의", 1L, 1, 2).copy(id = 2L))
        val child2 = CategoryNode(Category.create("상의", 1L, 1, 1).copy(id = 3L))
        val child3 = CategoryNode(Category.create("아우터", 1L, 1, 3).copy(id = 4L))
        val parentNode = CategoryNode(parentCategory)

        // When
        parentNode.addChild(child1) // sortOrder: 2
        parentNode.addChild(child2) // sortOrder: 1
        parentNode.addChild(child3) // sortOrder: 3

        // Then
        assertEquals(3, parentNode.children.size)
        assertEquals("상의", parentNode.children[0].category.name) // sortOrder: 1
        assertEquals("하의", parentNode.children[1].category.name) // sortOrder: 2
        assertEquals("아우터", parentNode.children[2].category.name) // sortOrder: 3
    }

    @Test
    fun `깊이 계산 테스트`() {
        // Given
        val root = CategoryNode(Category.create("의류", null, 0, 1).copy(id = 1L))
        val level1 = CategoryNode(Category.create("상의", 1L, 1, 1).copy(id = 2L))
        val level2 = CategoryNode(Category.create("티셔츠", 2L, 2, 1).copy(id = 3L))

        // When
        root.addChild(level1)
        level1.addChild(level2)

        // Then
        assertEquals(0, root.getDepth())
        assertEquals(1, level1.getDepth())
        assertEquals(2, level2.getDepth())
    }

    @Test
    fun `형제 노드 조회 테스트`() {
        // Given
        val parentCategory = Category.create("의류", null, 0, 1).copy(id = 1L)
        val child1 = CategoryNode(Category.create("상의", 1L, 1, 1).copy(id = 2L))
        val child2 = CategoryNode(Category.create("하의", 1L, 1, 2).copy(id = 3L))
        val child3 = CategoryNode(Category.create("아우터", 1L, 1, 3).copy(id = 4L))
        val parentNode = CategoryNode(parentCategory)

        // When
        parentNode.addChild(child1)
        parentNode.addChild(child2)
        parentNode.addChild(child3)

        val siblings = child2.getSiblings()

        // Then
        assertEquals(2, siblings.size)
        assertTrue(siblings.contains(child1))
        assertTrue(siblings.contains(child3))
        assertFalse(siblings.contains(child2)) // 자기 자신은 포함되지 않음
    }

    @Test
    fun `루트 노드의 형제 조회 테스트`() {
        // Given
        val rootNode = CategoryNode(Category.create("의류", null, 0, 1).copy(id = 1L))

        // When
        val siblings = rootNode.getSiblings()

        // Then
        assertTrue(siblings.isEmpty())
    }

    @Test
    fun `재귀적 자식 정렬 테스트`() {
        // Given
        val root = CategoryNode(Category.create("의류", null, 0, 1).copy(id = 1L))
        val level1_1 = CategoryNode(Category.create("하의", 1L, 1, 2).copy(id = 2L))
        val level1_2 = CategoryNode(Category.create("상의", 1L, 1, 1).copy(id = 3L))
        val level2_1 = CategoryNode(Category.create("긴바지", 2L, 2, 2).copy(id = 4L))
        val level2_2 = CategoryNode(Category.create("반바지", 2L, 2, 1).copy(id = 5L))

        // When
        root.addChild(level1_1)
        root.addChild(level1_2)
        level1_1.addChild(level2_1)
        level1_1.addChild(level2_2)

        root.sortChildrenRecursively()

        // Then
        // 1단계 정렬 확인
        assertEquals("상의", root.children[0].category.name)
        assertEquals("하의", root.children[1].category.name)

        // 2단계 정렬 확인
        val level1_1_sorted = root.children[1] // 하의
        assertEquals("반바지", level1_1_sorted.children[0].category.name)
        assertEquals("긴바지", level1_1_sorted.children[1].category.name)
    }

    @Test
    fun `노드 동등성 테스트`() {
        // Given
        val category1 = Category.create("의류", null, 0, 1).copy(id = 1L)
        val category2 = Category.create("의류", null, 0, 1).copy(id = 1L)
        val category3 = Category.create("신발", null, 0, 2).copy(id = 2L)

        val node1 = CategoryNode(category1)
        val node2 = CategoryNode(category2)
        val node3 = CategoryNode(category3)

        // When & Then
        assertEquals(node1, node2) // 같은 ID를 가진 노드는 동등
        assertNotEquals(node1, node3) // 다른 ID를 가진 노드는 다름
        assertEquals(node1.hashCode(), node2.hashCode()) // 같은 ID는 같은 해시코드
    }

    @Test
    fun `toString 테스트`() {
        // Given
        val category = Category.create("의류", null, 0, 1).copy(id = 1L)
        val node = CategoryNode(category)
        val child = CategoryNode(Category.create("상의", 1L, 1, 1).copy(id = 2L))
        node.addChild(child)

        // When
        val toString = node.toString()

        // Then
        assertTrue(toString.contains("의류"))
        assertTrue(toString.contains("children=1"))
    }
} 