--------------------------------------------------------------
Notes for regression test harness "regrtest":
--------------------------------------------------------------

--------------------------------------------------------------
Usage of regression tester:
Create and maintain a suite of test cases. Every time code is
changed, re-run the test suite to see if new code works and
old code still works.

It's good to know that your old code still works with absolutely
zero extra effort on your part.

Run with:
./regrtest
--------------------------------------------------------------

--------------------------------------------------------------
Files:
regrtest
tc001recv.java
tc001send.java
tc001/ (folder containing err.*, exp.*, inp.*, and out.* files.)
... other test files and folders beginning with tcxxx,
where xxx is a 3 digit number beginning with 001.

Files in test case folders:
err.recv.new
err.recv.old
err.send.new
err.send.old
exp.recv.new
exp.recv.old
exp.send.new
exp.send.old
inp.recv.new
inp.recv.old
inp.send.new
inp.send.old
out.recv.new
out.recv.old
out.send.new
out.send.old

There are files for errors (err), expected results (exp),
input (inp) and output (out).

For each type of file, there is a version for the
sender (send) and for the receiver (recv).

For each type of file, there is a new version for the
current execution of the regression test (new) and an
old version for the previous execution (old).
--------------------------------------------------------------

--------------------------------------------------------------
NOTES:
--------------------------------------------------------------
At each running of the regression tester, existing *.class
files are deleted. The test case .java files having the main
method (tcxxxrecv.java and tcxxxsend.java) are compiled with
javac.

The regression tester loops through all the folders named
tcxxx/, and for each one runs the tcxxxrecv program in the
background then the tcxxxsend program in the foreground.

The input to the test programs are the inp.recv.new and
inp.send.new files. Output goes in the out.recv.new and
out.send.new files. Errors go in err.recv.new and err.send.new.
Actual output is compared with expected output. New files are
compared with old versions. Errors are generated if
comparisons fail. The exit code is a count of failed
comparisons.
--------------------------------------------------------------

--------------------------------------------------------------
Example:

Run regression tester:
./regrtest

No output from the regression tester indicates no errors.
Do:
echo $?
to get the exit code, a count of failed file comparisons.

Example files from test folders:
err.recv.new, err.send.new:
(empty, unless test had errors. some tests are made to create errors.)

exp.recv.new, exp.send.new:
(should have same contents as out.recv.new and out.send.new, unless
test had errors. some tests are made to create errors.)

inp.recv.new
64002 127.0.0.1 65002 100 r

inp.send.new
65002 127.0.0.1 64002 100 s

out.recv.new
packet received: Number:  0
packet received: Number:  1
packet received: Number:  2
packet received: Number:  3
packet received: Number:  4
packet received: Number:  5
packet received: Number:  6
packet received: Number:  7
packet received: Number:  8
packet received: Number:  9
packet received: quit

out.send.new
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
--------------------------------------------------------------
--------------------------------------------------------------

