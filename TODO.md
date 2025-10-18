# Issues/TODO

## Strength:

## Code Quality:

- Remove code fragments at the bottom of files.
- Interface / impl pattern.
    - Convert classnames to "classnameImpl", and create interfaces named "classname"
    - Substitute implementations with their interfaces
    - Javadocs should be on the interface, not the impl
- "Evaluator" interface for NNUE
- Improve Javadocs
    - They should work and display on hover as intended in the code
- Improve unit testing
- Change print statements to log

## Features:

- Add HCE with option
- Improve PV Tree
- Remove "moveValue" class, prefer pv tree? or something else? Investigate this.
- Aspiration windows?

## Meta/Other:

- Create version control for external tools/scripts to make development more flexible on different machines

## Misc/Questionable:

- Refactor Search to an object, that contains (EnchineCache, formerly PositionState)
