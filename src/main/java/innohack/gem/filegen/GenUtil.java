package innohack.gem.filegen;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class GenUtil {
    static Random random = new Random();

    static String[] tokens = ("Alice was beginning to get very tired of sitting by her sister on the bank," +
            " and of having nothing to do: once or twice she had peeped into the book her sister " +
            "was reading, but it had no pictures or conversations in it, 'and what is the use of a " +
            "book,' thought Alice 'without pictures or conversations?'  So she was considering in her" +
            " own mind (as well as she could, for the hot day made her feel very sleepy and stupid), " +
            "whether the pleasure of making a daisy-chain would be worth the trouble of getting up and" +
            " picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.  " +
            "There was nothing so VERY remarkable in that; nor did Alice think it so VERY much out of " +
            "the way to hear the Rabbit say to itself, 'Oh dear! Oh dear! I shall be late!' (when she" +
            " thought it over afterwards, it occurred to her that she ought to have wondered at this, " +
            "but at the time it all seemed quite natural); but when the Rabbit actually TOOK A WATCH " +
            "OUT OF ITS WAISTCOAT-POCKET, and looked at it, and then hurried on, Alice started to her" +
            " feet, for it flashed across her mind that she had never before seen a rabbit with either " +
            "a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across" +
            " the field after it, and fortunately was just in time to see it pop down a large rabbit-hole")
            .split("\\s+");

    static String[] names = ("OLIVIA RUBY EMILY GRACE JESSICA CHLOE SOPHIE LILY AMELIA EVIE MIA ELLA " +
            "CHARLOT LUCY MEGAN ELLIE ISABELL ISABELL HANNAH KATIE AVA HOLLY SUMMER MILLIE DAISY PHOEBE " +
            "FREYA ABIGAIL POPPY ERIN EMMA MOLLY IMOGEN AMY JASMINE ISLA SCARLET LEAH SOPHIA ELIZABE EVA " +
            "BROOKE MATILDA CAITLIN KEIRA ALICE LOLA LILLY AMBER ISABEL LAUREN GEORGIA GRACIE ELEANOR " +
            "BETHANY MADISON AMELIE ISOBEL PAIGE LACEY SIENNA LIBBY MAISIE ANNA REBECCA ROSIE TIA LAYLA MAYA NIAMH")
            .split("\\s+");

    public static String randomName() {
        return names[randomInt(names.length)];
    }

    public static int randomInt(int bound) {
        return random.nextInt(bound);
    }

    public static int randamIntRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static String randomWord() {
        return tokens[randomInt(tokens.length)];
    }

    public static String oneOf(String... options) {
        return options[randomInt(options.length)];
    }

    public static String randomString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append((char)('a' + randomInt(26)));
        }
        return sb.toString();
    }

    public static void writeToFile(List<String> row, Path dest, String filename) throws FileNotFoundException {
        Path path = Paths.get(dest.toString(), filename);
        try (PrintWriter out = new PrintWriter(path.toFile())) {
            for (String line: row) {
                out.println(line);
            }
        }
    }
}
