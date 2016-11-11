#include "Object.h"
/*  The general object header file to hold data in the node of the linked list
@author kgross

*/

/* constructor
ObjectPtr createObject(const int key, const char *data)
@parms the key and data

creates a new object with the key and data for the object


*/
ObjectPtr createObject(const int key, const char *data)
{
    ObjectPtr newObject = (ObjectPtr) malloc(sizeof(Object));
    newObject->key = key;
    newObject->data = (char *) malloc(sizeof(char) * (strlen(data) + 1));
    strcpy(newObject->data, data);
    return newObject;
}

/* int compairTo(const void *obj, const void *other)
@parms pointer to the objects to compare
returns true (1) if they have the same key

*/


int compareTo(const void *obj, const void *other)
{
    ObjectPtr o1 = (ObjectPtr) obj;
    ObjectPtr o2 = (ObjectPtr) other;
    return o1->key == o2->key;
}

/* char *toString (const void *obj)
@parms pointer to the objects to print
returns the pid of the object ready to print.

*/
char *toString(const void *obj)
{
    ObjectPtr myobj = (ObjectPtr) obj;
    char *temp;
    temp =
	(char *) malloc(sizeof(char) * strlen(myobj->data) + 1 +
			MAXPID_DIGITS + 4);
    sprintf(temp, "[%d] %s", myobj->key, myobj->data);
    return temp;
}

/*  void freeObject (const void *obj)
@parm, pointer to the object
frees the memory allocated to the object.

*/
void freeObject(const void *obj)
{
    ObjectPtr myobj = (ObjectPtr) obj;
    if (myobj == NULL)
	return;
    free(myobj->data);
    free(myobj);
}
