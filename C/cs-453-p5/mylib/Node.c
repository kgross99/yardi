#include "Node.h"




//void freeNode(NodePtr node)
//{
//    if (node == NULL)
//	return;
//    freeJob(node->data);
//    free(node);
//}
NodePtr createNode( void *obj)
{
NodePtr newNode = (NodePtr) malloc (sizeof(Node));
newNode->next = NULL;
newNode->prev = NULL;
newNode->obj = obj;
return newNode;
}
void freeNode (NodePtr node, void (*freeObject)(const void * ) )
{
if (node == NULL) return;
(*freeObject)(node->obj);
free(node);
}
