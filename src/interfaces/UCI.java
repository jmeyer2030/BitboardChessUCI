package interfaces;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
public class UCI {
/*
UCI Protocol implemented per:
https://www.wbec-ridderkerk.nl/html/UCIProtocol.html
https://gist.github.com/DOBRO/2592c6dad754ba67e6dcaec8c90165bf

notes:
 - extra white space should be ignored
 - arbitrary tokens before the first command should be ignored
 - all inputs and outputs end with a "\n"
 - moves are in long algebraic notation, e.g. "e7e8q" for a promotion, or just "<start><destination>" for any non-promotion move


*/
    public void inputLoop() {
        while (true) {
            List<String> commandParts = getCommand();

            handleCommand(commandParts);
        }
    }

    /**
    * Retrieves the next standard input, separates tokens into a list removing whitespace, and returns it
    * @return list of command tokens
    */
    public List<String> getCommand() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        List<String> parts = Arrays.asList(input.split("\\s+"));
        return parts;
    }

    /**
    * Handles a command
    */
    public void handleCommand(List<String> parts) {
        // Get the command token
        while (parts.size() > 0) {
            switch (parts.getFirst()) {
                case "debug" :
                    debug();
                    break;
                case "isready" :
                    isReady();
                    break;
                case "setoption " :
                    setOption();
                    break;
                case "register" :
                    register();
                    break;
                case "ucinewgame" :
                    uciNewGame();
                    break;
                case "position" :
                    position();
                    break;
                case "go" :
                    go();
                    break;
                case "stop" :
                    stop();
                    break;
                case "ponderhit" :
                    ponderHit();
                    break;
                case "quit" :
                    quit();
                    break;
                default :
                    break;
            }
        }

    }

    public void debug() {

    }

    public void isReady() {

    }

    public void setOption() {

    }

    public void register() {

    }

    public void uciNewGame() {

    }

    public void position() {

    }

    public void go() {

    }

    public void stop() {

    }

    public void ponderHit() {

    }

    public void quit() {

    }

    public void invalidCommand() {
    }
}
