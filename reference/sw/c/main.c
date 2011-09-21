#include <stdio.h>
#include <stdlib.h>
#include "parser.tab.h"
#include "log.h"
#include "stack.h"
#include "symbol.h"

/* for getopts */
#include <ctype.h>
#include <unistd.h>

int LOG_LEVEL = 0;

static char *S_FILE_INPUT = NULL;
static char *S_FILE_OUTPUT = NULL;
static char *S_PARSE_OUTPUT = NULL;
static char *S_SYMBOL_OUTPUT = NULL;
static FILE *FILE_INPUT = NULL;
static FILE *FILE_OUTPUT = NULL;
static FILE *PARSE_OUTPUT = NULL;
static FILE *SYMBOL_OUTPUT = NULL;

stack *parse_stack = NULL;
symbol_table *sym = NULL;

void print_usage(void) {
	printf("pcc - plp c compiler\n\n");
	printf("usage: pcc <options> <input file(s)>\n");
	printf("options:\n");
	printf("-o <filename>	output filename\n");
	printf("-d [0,1,2]	debug level (0=off (default), 1=on, 2=verbose)\n");
	printf("-s		print symbol table to <output name>.symbol\n");
	printf("-p		print parse stack to <output name>.parse\n");
}

void handle_opts(int argc, char *argv[]) {
	char *dvalue = NULL;
	char *ivalue = NULL;
	char *ovalue = NULL;

	int pparse = 0;
	int psymbol = 0;

	int c;

	opterr = 0;
	
	while ((c = getopt(argc, argv, "d:o:sp")) != -1)
		switch (c) {
			case 'd':
				dvalue = optarg;
				break;
			case 'o':
				ovalue = optarg;
				break;
			case 's':
				psymbol = 1;
				break;
			case 'p':
				pparse = 1;
				break;
			default:
				print_usage();
				exit(1);
		}

	if (dvalue != NULL) {
		LOG_LEVEL = atoi(dvalue);
		vlog("[pcc] setting log level: %d\n", LOG_LEVEL);
	}

	/* grab remaining values as inputs */
	if (optind < argc) {
		while (optind < argc) {
			ivalue = argv[optind];
			log("[pcc] input file: %s\n", argv[optind]);
			optind++;
		}
	}

	if (ivalue != NULL) {
		S_FILE_INPUT = ivalue;
	} else {
		print_usage();
		exit(1);
	}

	if (ovalue != NULL) {
		S_FILE_OUTPUT = ovalue;
	} else {
		S_FILE_OUTPUT = malloc(9);
		sprintf(S_FILE_OUTPUT,"pcc.out");
	}
	log("[pcc] output file: %s\n", S_FILE_OUTPUT);

	if (psymbol) {
		S_SYMBOL_OUTPUT = malloc(sizeof(char) * (strlen(S_FILE_OUTPUT) + 8));
		sprintf(S_SYMBOL_OUTPUT, "%s.symbol", S_FILE_OUTPUT);
		log("[pcc] symbol table output: %s\n", S_SYMBOL_OUTPUT);
	}

	if (pparse) {
		S_PARSE_OUTPUT = malloc(sizeof(char) * (strlen(S_FILE_OUTPUT) + 7));
		sprintf(S_PARSE_OUTPUT, "%s.parse", S_FILE_OUTPUT);
		log("[pcc] parse table output: %s\n", S_PARSE_OUTPUT);
	}
	
}

int main(int argc, char *argv[]) {

	/* process arguments */
	handle_opts(argc, argv);	

	/* open files */
	vlog("[pcc] opening files\n");
	FILE_INPUT = fopen(S_FILE_INPUT,"r");
	if (FILE_INPUT == NULL) {
		err("[pcc] cannot open input file: %s\n", S_FILE_INPUT);
		exit(1);
	}
	FILE_OUTPUT = fopen(S_FILE_OUTPUT,"w");
	if (FILE_OUTPUT == NULL) {
		err("[pcc] cannot open output file: %s\n", S_FILE_OUTPUT);
		exit(1);
	}
	yyset_in(FILE_INPUT);

	if (S_SYMBOL_OUTPUT != NULL) {
		SYMBOL_OUTPUT = fopen(S_SYMBOL_OUTPUT, "w");
		if (SYMBOL_OUTPUT == NULL) {
			err("[pcc] cannot open symbol table output file: %s\n", S_SYMBOL_OUTPUT);
			exit(1);
		}
	}

	if (S_PARSE_OUTPUT != NULL) {
		PARSE_OUTPUT = fopen(S_PARSE_OUTPUT, "w");
		if (PARSE_OUTPUT == NULL) {
			err("[pcc] cannot open parse table output file: %s\n", S_PARSE_OUTPUT);
			exit(1);
		}
	}

	/* create an empty symbol table */
	sym = new_symbol_table(NULL);

	log("[pcc] starting frontend\n");
	yyparse();

	/* print the parse stack */
	if (PARSE_OUTPUT != NULL)
		print_stack(PARSE_OUTPUT);

	vlog("[pcc] closing files\n");
	fclose(FILE_INPUT);
	fclose(FILE_OUTPUT);

	log("[pcc] done\n");
	return 0;
}
