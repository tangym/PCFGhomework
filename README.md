PCFGhomework
============

Created by TYM 
in THU Prof. Y.Liu NLP Class.

2013-12-17

version 1.0.0

The program reads file "training set.txt", counts the probabilities of each 
model, and writes them into file "model.txt".

Then it finds the most likely parse of sentence "A boy with a telescope saw a 
girl", which is stored in the String array sentence[]. The most likely parse is 
calculated using Viterbi algorithm.

Finally, the program writes the parse, the probability of this parse and inside 
& outside probabilities of all spans into file "parse.txt".


PS:

File "model.txt" format

S # NP VP # 1.0 // tag word prob

VBD # saw # 1.0 // LHS RHS prob

...


File "parse.txt" format

(S(NP(DT a)...) // the most likely parse

0.00066 // the highest probability

DT # 0 # 0 # 0.66667 # 0.00099 // Nj p q insideProb outsideProb

NP # 0 # 1 # 0.19048 # 0.00346

...
