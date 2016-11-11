

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <stdint.h>
#include <sys/types.h>
#include <pthread.h>
#include "buddy.h"

static void *pool = NULL;
static Boolean init = FALSE;
static struct block_header *buddy_headers[30];
static pthread_mutex_t buddy_mutex = PTHREAD_MUTEX_INITIALIZER;
static int debug = 0;

void *malloc(size_t size)
{
    if (debug) {
	printf("calling malloc\n");
    }
    void *temp;
    pthread_mutex_lock(&buddy_mutex);
    temp = buddy_malloc(size);
    pthread_mutex_unlock(&buddy_mutex);
    return temp;
}

void *realloc(void *ptr, size_t size)
{

    if (debug) {
	printf("calling realloc\n");
    }
    void *temp;
    pthread_mutex_lock(&buddy_mutex);
    temp = buddy_realloc(ptr, size);
    pthread_mutex_unlock(&buddy_mutex);


    return temp;
}

void *calloc(size_t nitems, size_t item_size)
{

    if (debug) {
	printf("calling calloc\n");
    }

    void *temp;
    pthread_mutex_lock(&buddy_mutex);
    temp = buddy_calloc(nitems, item_size);
    pthread_mutex_unlock(&buddy_mutex);



    return temp;
}

void free(void *ptr)
{
    if (debug) {
	printf("calling free\n");
    }
    pthread_mutex_lock(&buddy_mutex);
    buddy_free(ptr);
    pthread_mutex_unlock(&buddy_mutex);



}

/* void buddy_init (size_t size)
@parms size.... not used.  
buddy_init initializes the the memory manager by obtaining memory via sbrk


*/
void buddy_init(size_t size)
{
    if (debug) {
	printf("calling buddy init\n");
    }
    pool = sbrk(BUDDY_POOL_SIZE);
    if (pool < 0 || errno == ENOMEM) {
	perror("Could not allocate memory pool!");
	exit(1);
    }

    int i;
    for (i = 0; i < 29; i++) {
	buddy_headers[i] = NULL;
    }
    buddy_headers[29] = pool;
    buddy_headers[29]->data.kval = 29;
    buddy_headers[29]->data.tag = TRUE;
    init = TRUE;
}

/* void * buddy_calloc(size_t items, size_t size)
@parms items number of items to init,  size, size in bytes of the memory block needed
@returns  void * pointer to the begining of the memory block

*/


void *buddy_calloc(size_t items, size_t size)
{
    if (!init) {
	buddy_init(0);
    }
    void *mem_block = buddy_malloc(items * size);

    if (mem_block) {
	memset(mem_block, 0, items * size);
	return mem_block;
    } else {
	return NULL;
    }

    return NULL;
}

/* void * buddy_malloc(size_t size)
@parms. size, the amount of memory needed in bytes
@returns void * ptr to the begining of the memory block


*/
void *buddy_malloc(size_t size)
{
    if (!init) {
	buddy_init(0);
    }

    int required_block_size = size + sizeof(struct block_header);

    int i;
    for (i = 5; i <= 29; i++) {
	// Potential fit.
	if (required_block_size < (1 << i)) {
	    // Is there a block available?
	    if (buddy_headers[i] != NULL
		&& buddy_headers[i]->data.tag == TRUE) {
		// Iterate over the buddy_headers starting at i, splitting as we need.
		int j;
		for (j = i; j > 4; j--) {
		    // Begin splitting until we find the correct size of block.
		    if (required_block_size * 2 <
			(1 << buddy_headers[j]->data.kval)) {
			buddy_headers[j - 1] = buddy_headers[j];
			if ((buddy_headers[j] =
			     buddy_headers[j]->data.next)) {
			    buddy_headers[j]->data.prev = NULL;
			} else {
			    buddy_headers[j] = NULL;
			}
			buddy_headers[j - 1]->data.kval = j - 1;
			buddy_headers[j - 1]->data.tag = TRUE;
			buddy_headers[j - 1]->data.prev = NULL;
			buddy_headers[j - 1]->data.next =
			    (struct block_header *) ((char *)
						     buddy_headers[j - 1] +
						     (1 << (j - 1)));
			buddy_headers[j - 1]->data.next->data.kval = j - 1;
			buddy_headers[j - 1]->data.next->data.tag = TRUE;
			buddy_headers[j - 1]->data.next->data.prev =
			    buddy_headers[j - 1];
			buddy_headers[j - 1]->data.next->data.next = NULL;
		    } else {
			// We found the block, remove and return.
			void *address =
			    (struct block_header *) ((char *)
						     buddy_headers[j] +
						     sizeof(struct
							    block_header));
			buddy_headers[j]->data.tag = FALSE;
			buddy_headers[j] = buddy_headers[j]->data.prev;
			if (buddy_headers[j]) {
			    buddy_headers[j]->data.next = NULL;
			}
			return address;
		    }
		}
	    } else {
		// No available block here, move on to the next.
		continue;
	    }
	    //Larger memory than we can allocate.
	    errno = ENOMEM;
	    return NULL;
	}
    }

    return NULL;
}

/* void * buddy_realloc(void * ptr, size_t size)
@parms.  ptr, pointer to existing memory block, size, size needed for new memory block
@returns  void * ptr to the new memory block
Buddy_realloc if the ptr is null does a normal malloc
if the ptr is not null, it allocates a new block of memory of size, size, and copies the data from the old address to the new
free's old ptr

*/

void *buddy_realloc(void *ptr, size_t size)
{
    if (!init) {
	buddy_init(0);
    }
    if (debug) {
	printf("realloc ptr %ld size %ld\n", (long int) ptr, size);
    }

    if (!ptr) {
	return buddy_malloc(size);
    }

    if (size == 0) {
	free(ptr);
	return NULL;
    }

    if (debug) {
	printf(" valid ptr and block size, time to resize\n");
    }
    struct block_header *current_block =
	(struct block_header *) (ptr - sizeof(struct block_header));

    if (current_block && size > 0) {

	if (debug) {
	    printf("realloc checking size of current block\n");
	}
	int kval = current_block->data.kval;
	if (debug) {
	    printf("kval is %d\n", kval);
	}


	void *new_block = buddy_malloc(size);
	if (debug) {
	    printf("calling buddy malloc for new block\n");
	}

	if (new_block) {
	    if (debug) {
		printf("calling memcpy for new block\n");
	    }

	    memcpy(new_block, ptr,
		   (1 << kval) - sizeof(struct block_header));
	    if (debug) {
		printf("finished memcpy for new block\n");
	    }


	    buddy_free(ptr);
	    return new_block;
	}
    }
    return NULL;
}


/* void buddy_free(void *ptr)
@parms  ptr,   pointer to a buddy_malloc block of memory
@returns void
buddy_free releases the memory assigned to the ptr back into the heap


*/

void buddy_free(void *ptr)
{
    if (!init || !ptr) {
	return;
    }

    if (debug) {
	printf("calling buddy free\n");
    }

    struct block_header *block = (ptr - sizeof(struct block_header));

    block->data.next = NULL;
    block->data.prev = NULL;
    if (debug) {
	printf("calling merge_r\n");
	printf("with block kval %ld tag %ld ", (long int) block->data.kval,
	       (long int) block->data.tag);
    }



    merge_r(block);
    if (debug) {
	printf("leaving buddy free\n");
    }

    if (debug) {
	printf("calling buddy free\n");
    }
    if (debug) {
	printBuddyLists();
    }


}

/* void merge_r(struct block_header * block)
@parms  struct block_header,  the header to a block of buddy memory
Merge_r tries to merge the freed block of memory with its buddy and move it up in size to the largest block possible

*/


void merge_r(struct block_header *block)
{
    if (debug) {
	printf("entering merge_r\n");
    }


    if (debug) {
	printBuddyLists();
    }

    struct block_header *buddy =
	(struct block_header
	 *) (((size_t) (((void *) block)) -
	      (size_t) pool) ^ (1 << block->data.kval));
    if (debug) {
	printf("inside of merge getting correct header kval %ld\n",
	       (long int) block->data.kval);
	printf("block kval %ld tag %ld next %ld Prev %ld ",
	       (long int) block->data.kval, (long int) block->data.tag,
	       (long int) block->data.next, (long int) block->data.prev);
    }
    block->data.tag = 1;
    struct block_header *curr = buddy_headers[block->data.kval];
    if (debug) {
	printf("looking at merging\n");
    }
    while (curr) {
	if ((void *) (((void *) curr) - pool) == (void *) buddy) {
	    struct block_header *joined;
	    if (curr->data.next) {
		curr->data.next->data.prev = curr->data.prev;
	    }
	    if (curr->data.prev) {
		curr->data.prev->data.next = curr->data.next;
	    } else {
		buddy_headers[curr->data.kval] = curr->data.next;
	    }
	    if (debug) {
		printf("here\n");
	    }
	    if (debug) {
		printf("merging\n");
	    }
	    curr->data.prev = NULL;
	    curr->data.next = NULL;
	    if (block < curr) {
		joined = block;
	    } else {
		joined = curr;
	    }
	    joined->data.kval++;
	    return merge_r(joined);
	}
	curr = curr->data.next;
    }
    if (debug) {
	printf("here\n");
    }
    if (buddy_headers[block->data.kval]) {
	if (debug) {
	    printf("inside here\n");
	}
	buddy_headers[block->data.kval]->data.prev = block;
	block->data.next = buddy_headers[block->data.kval];
    }
    if (debug) {
	printf("at end of merge kval %ld  block  %ld\n",
	       (long int) block->data.kval, (long int) block);
    }
    buddy_headers[block->data.kval] = block;
    if (debug) {
	printf("leaving merge\n");
    }

    if (debug) {
	printBuddyLists();
    }

}

/* void printBuddyLists(void)
@parms void

prints out the list of buddy blocks available




*/
void printBuddyLists()
{
    if (!init) {
	printf("No pool initialized.\n");
    }

    int i;
    for (i = 0; i <= 29; i++) {
	int j = 0;
	struct block_header *curr = buddy_headers[i];
	if (curr == NULL) {
	    printf("%d", j);
	    printf("[KVAL: %d, ADDR: %d, TAG: %d]\n", i, -1, 0);
	    continue;
	} else {
	    while (curr) {
		printf("%d", j);
		printf
		    ("[KVAL: %d, ADDR: %p, TAG: %d, NEXT: %p, PREV: %p]\n",
		     curr->data.kval, curr, curr->data.tag,
		     curr->data.next, curr->data.prev);

		curr = curr->data.next;
		j++;
	    }
	}
    }

    printf("DONE WITH LIST\n");
}

/* void printBuddyNode (int i)
@parms the kval of the list to print
Prints out the data for the list of blocks at kval of i

*/
void printBuddyNode(int i)
{
    if (!buddy_headers[i]) {
	printf("NULL NODE AT %d\n", i);
	return;
    }
    printf("[KVAL: %d, ADDR: %p, TAG: %d, NEXT: %p, PREV: %p]\n",
	   buddy_headers[i]->data.kval,
	   buddy_headers[i],
	   buddy_headers[i]->data.tag,
	   buddy_headers[i]->data.next, buddy_headers[i]->data.prev);
}
