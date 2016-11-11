#include <stdio.h>
#include <stdlib.h>
#include "common.h"
#include "List.h"
#include "WordObj.h"
ListPtr templist;
// NodePtr tempNode, currentNode;
WordObjPtr tempJob;

static void print(const NodePtr node, char * (*toString)(const void *));

ListPtr createList(int(*compareTo)(const void *),
                   char * (*toString)(const void *),
		   void (*freeObject)(const void *))
{
	ListPtr list;
	list = (ListPtr) malloc(sizeof(List));
	list->size = 0;
	list->head = NULL;
	list->tail = NULL;
	list->compareTo = (void *)compareTo;
	list->toString = (void *)toString;
	list->freeObject = (void *)freeObject;
	return list;
}



void freeList(ListPtr L) {
	NodePtr tempNode, currentNode;
	if (L==NULL){
		return;
	}

	if (L->head == NULL ) {
		free(L);
		return;
	}

	if (L->head == L->tail) {
		freeNode(L->head, (void *) L->freeObject);
		free(L);
		return;
	}

	currentNode = L->head;
	while (currentNode != NULL ) {
		tempNode = currentNode;
		currentNode = currentNode->next;

		freeNode(tempNode, (void *) L->freeObject);
	}

	free(L);
}

int getSize(ListPtr L) {
	if (L==NULL){
		return 0;
	}

	return L->size;
}

Boolean isEmpty(ListPtr L) {

	if (L == NULL ) {
		return TRUE;
	}
	if (L->head == NULL ) {
		return TRUE;
	}
	return FALSE;
}

void addAtFront(ListPtr list, NodePtr node) {
	NodePtr tempNode;
	if (list == NULL )
		return;
	tempNode = list->head;

	if (node == NULL )
		return;

	if (tempNode == NULL ) {
		list->head = node;
		list->tail = node;
		node->next = NULL;
		node->prev = NULL;
	} else {
		node->next = list->head;
		node->prev = NULL;
		tempNode->prev = node;
		list->head = node;
	}
	list->size++;
	//free(tempNode);
	return;
}

void addAtRear(ListPtr list, NodePtr node) {
	if (list == NULL )
		return;
	if (node == NULL )
		return;
	if (list->head == NULL ) {
		list->head = node;
		list->tail = node;
		node->next = NULL;
		node->prev = NULL;
		list->size++;
	} else {
		node->prev = list->tail;
		list->tail->next = node;
		node->next = NULL;
		list->tail = node;
		list->size++;

	}
}

NodePtr removeFront(ListPtr list) {
	NodePtr tempNode;
	if (list == NULL ) {
		return NULL ;
	}
	if (list->head == NULL )

	{
		return NULL ;
	}

	if (list->head == list->tail) {
		tempNode = list->head;
		list->head = NULL;
		list->tail = NULL;
		list->size = 0;
//		printf(" access removeFront with head=tail\n");
		return tempNode;
	}
	tempNode = list->head;

	list->head = list->head->next;
	list->head->prev=NULL;
	list->size--;
//	printf("access remove front with more than one node\n");
	return tempNode;
}

NodePtr removeRear(ListPtr list) {
	NodePtr tempNode;
	if (list == NULL ) {
		return NULL ;
	}
	if (list->head == NULL ) {
		return NULL ;
	}
	if (list->head == list->tail) {
		list->size = 0;
		tempNode = list->head;
		list->head = NULL;
		list->tail = NULL;
		return tempNode;

	}
	tempNode = list->tail;
	list->tail = tempNode->prev;
	list->tail->next = NULL;
	list->size--;
	return tempNode;

}

NodePtr removeNode(ListPtr list, NodePtr node) {
	NodePtr tempNode;
	if (list == NULL){
		return NULL;
	}
	if (node == NULL ) {

		return NULL ;
	}
	if (node->next==NULL){
		if (node->prev==NULL){
			return NULL;
		}
	}
	if (list->size == 1) {
		list->size = 0;
		list->head = NULL;
		list->tail = NULL;
		return node;
	}
	if (node->prev==NULL){
		list->head=node->next;
		tempNode=list->head;
		tempNode->prev=NULL;
		return node;
	}
	if (node->next==NULL){
		tempNode=list->tail;
		tempNode->prev->next=NULL;
		list->tail=tempNode->prev;
		return node;
	}

	tempNode = node->prev;
	tempNode->next = node->next;
	node->next->prev = tempNode;
	list->size--;
	return node;

}

NodePtr search(ListPtr list, const void * o) {
	NodePtr tempNode;
	if (list == NULL ) {
		return NULL ;
	}
	if (list->head == NULL ) {
		return NULL ;
	}
	tempNode = list->head;
	while (tempNode->next) {


		if (list->compareTo(( void *)o,( void *)tempNode->obj)==0) {
			return tempNode;

		}
		tempNode = tempNode->next;
	}

	return NULL ;
}

void reverseList(ListPtr L) {
	NodePtr currentNode, tempNode;

	if (L == NULL ) {
		return;
	}
	if (L->head== NULL){
		return;
	}
	if (L->head == L->tail) {
		return;
	}

	currentNode = L->head;
	while (currentNode) {
		tempNode = currentNode->next;
		currentNode->next = currentNode->prev;
		currentNode->prev = tempNode;
		currentNode = currentNode->prev;
	}
	tempNode = L->head;
	L->head = L->tail;
	L->tail = tempNode;


}

void printList(const ListPtr list)
{
	if (list) print(list->head, (void *)list->toString);
}

static void print(const NodePtr node, char * (*toString)(const void *))
{
	int count = 0;
	char *output;
        NodePtr temp = node;
	while (temp) {
		output = (*toString)(temp->obj);
		printf(" %s \n",output);
		free(output);
		temp = temp->next;
		count++;

	}

}






