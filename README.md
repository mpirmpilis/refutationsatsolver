## Propositional Logic Satisfiability (SAT) Solver
This developed specialized tool aims to solve propositional logic satisfiability problems for the **CS180 - Logic** course of the University of Crete (https://www.csd.uoc.gr/~hy180/) using the refutation systems method (Greek: **Κατασκευή Μοντέλων**).

Application comes in two versions: Offline standalone desktop version and Web version.

### Problem input instructions
Input may be given in the following three (3) forms:
```
1. {P->(Q|R), Q->S, R->S}/P->S    # Combination of {...}/     [brackets + slash form]
2. {~P|Q,P,~Q}                    # Only {...}                [brackets only form]
3. /(~P->~Q)->((~P->Q)->P)        # Only /                    [slash only form  --  used for proofs]
```
Use parenthesis to avoid ambiguity.

**Acceptable characters and operators**
```
Only latin capital/lowercase letters for variables    (A....Z and a....z)
~     (Logical NOT   ¬)
&     (logical AND   ∧)
|     (logical OR    ∨)
->    (If…Then…      →)
<->   (If only if    ↔)

Numbers and any other symbols are not accepted!
```
### Installation guide
The Project can be imported in Eclipse in the following way:

1) File  (upper left corner)
2) Open projects from file system…
3) At “Import source”, click Directory…
4) Select the top-level folder of the project (the one that contains the src, build and other files/folders) and click Select Folder
How to execute both versions:
  • Desktop version (offline)
   In order execute the jar file:
    Double-click on .jar file or  open cmd -> Change to jar's directory --> type java -jar NameOftheJarFile.jar
    *Using the cmd method will display any errors if something doesn't work correctly.
    Java version must be ≥ 1.8, otherwise it cannot be run.

  • Web version (online)
   The only thing that has to be configured is an application server (e.g. Tomcat) which is specifically designed to
   run Servlets and JAVA Server Pages that are based on web-applications. The project’s version was Tomcat v10.0
   Server. After successfully configuring the Tomcat server and deploy the application, open index.html and type
   a problem. The solution will be displayed in the same page.
