
CC=gcc
INSTALL=.
INCLUDE=$(INSTALL)/include -I.
LIBS=$(INSTALL)/lib -lreadline 
LFLAGS=-L$(LIBS) -Wl,-rpath=$(LIBS) -lmylib
CFLAGS=-Wall -g  -I$(INCLUDE) 
PROGS=library loop mydash

all: $(PROGS)
library:
	cd libsrc; make install; cd ..; cd buddy; make; cd ..

mydash:   svn_version.o  error.o mydash.o
	$(CC) $(CFLAGS) -o $@ $^ $(LFLAGS)

loop: loop.o
	$(CC) $(CFLAGS) -o $@  $<

##
## on every build, record the working copy revision string
##
svn_version.c: FORCE
		echo -n 'const char* svn_version(void) { const char* SVN_Version = "' > svn_version.c
		svnversion -n . >> svn_version.c
		echo '"; return SVN_Version; }' >> svn_version.c
			
FORCE:
##
## Then any executable that links in svn_version.o will be able
## to call the function svn_version() to get a string that
## describes exactly what revision was built.

clean:
	/bin/rm -f *.o $(PROGS) a.out core 

