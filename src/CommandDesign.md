# Command Handling Design

## Requirements:

- An initial set of commands should first be accepted
  - perft
  - uci
  - etc.
- Some of these commands should change the accepted set of commands to another set
- Some of these commands should remain the same


### How do we handle changing sets of commands that we should accept?

create commands that implement the Command interface.
Create lists of commands in the CommandHandler, only commands
in the currentCommands hashMap will be accepted.

## Classes

### Command Interface:
 - All commands implement this interface, 

### CommandHandler:

 - Maintains lists of accepted commands
 - Executes commands
 - Collects standard input, parses commands, validates commands

## UCI State:
 - Maintains the engine UCI state


##  