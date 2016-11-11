
#include "Node.h"
/* The node class header file to be used in my doubly-linked list
	@author kgross
*/


/* NodePtr createNode(const void *obj)
Constructor.  need to pass it the object pointer for the object it will hold

*/
NodePtr createNode(const void *obj)
{
    NodePtr newNode = (NodePtr) malloc(sizeof(Node));
    newNode->next = NULL;
    newNode->prev = NULL;


    newNode->obj = (void *) obj;
    return newNode;
}

/* void freeNode (const NodePtr node, void (*freeObject)(const void *))
Free's all the memory mallaced by the opbject in the node (via the freeObject pointer, and then the memory of the node itself.



*/
void freeNode(const NodePtr node, void (*freeObject) (const void *))
{
    if (node == NULL)
	return;
    (*freeObject) (node->obj);
    free(node);
}
