
#!/bin/sh

#$ -S /bin/sh

#$ -N perc.18617

# Parallel Environment Request
#$ -pe mpich 1

export PATH=$PATH:/net/maccoss/vol2/software/bin

which percolator; 
sqt2pin /net/maccoss/vol2/home/frewen/Research/hermie/data/test/barista-tests/pipeline/percolator/realSqt.list /net/maccoss/vol2/home/frewen/Research/hermie/data/test/barista-tests/pipeline/percolator/randSqt.list > psms.pin.xml
percolator -v 2  -M -E psms.pin.xml -X combined-results.perc.xml >> perc-messages 2> perc-stder && touch perc.18617.success
