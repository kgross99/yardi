



void trim_leading_and_trailing_whitespace(char *);

int check_for_background(char *);

int check_version(const char *);

int decode_command(char *, ListPtr);

void update_jobs_status(ListPtr, int);

int getjobid(ListPtr);

char *svn_version(void);
