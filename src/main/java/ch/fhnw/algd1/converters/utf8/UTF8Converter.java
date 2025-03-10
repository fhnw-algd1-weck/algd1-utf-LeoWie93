package ch.fhnw.algd1.converters.utf8;

public class UTF8Converter {

    // big if else. but I could not be bothered
    public static byte[] codePointToUTF(int x) {
        byte[] b;

        String binaryString = intToBinaryString(x);

        if (x <= 1 << 7) {
            b = new byte[1];
            b[0] = (byte) x;

        } else if (x <= 1 << 11) {
            b = new byte[2];

            String followUp = binaryString.substring(binaryString.length() - 6);
            String main = binaryString.substring(0, binaryString.length() - 6);

            b[1] = (byte) binaryStringToInt("10" + followUp);

            int padding = 5 - main.length();
            main = "110" + "0".repeat(padding) + main;
            b[0] = (byte) binaryStringToInt(main);

        } else if (x <= 1 << 16) {
            b = new byte[3];

            String followUpPart = binaryString.substring(binaryString.length() - 12);
            b[2] = (byte) binaryStringToInt("10" + followUpPart.substring(followUpPart.length() - 6));
            b[1] = (byte) binaryStringToInt("10" + followUpPart.substring(0, followUpPart.length() - 6));

            String main = binaryString.substring(0, binaryString.length() - 12);
            int padding = 4 - main.length();
            main = "1110" + "0".repeat(padding) + main;
            b[0] = (byte) binaryStringToInt(main);

        } else if (x <= 1 << 21) {
            b = new byte[4];

            if (binaryString.length() == 17) {
                binaryString = "0" + binaryString;
            }

            String followUpPart = binaryString.substring(binaryString.length() - 18);
            String main = binaryString.substring(0, binaryString.length() - 18);

            b[3] = (byte) binaryStringToInt("10" + followUpPart.substring(followUpPart.length() - 6));
            b[2] = (byte) binaryStringToInt("10" + followUpPart.substring(6, binaryString.length() - 6));
            b[1] = (byte) binaryStringToInt("10" + followUpPart.substring(0, 6));

            int padding = 3 - main.length();
            main = "11110" + "0".repeat(padding) + main;
            b[0] = (byte) binaryStringToInt(main);

        } else {
            return new byte[]{0};
        }

        return b;
    }

    public static int UTFtoCodePoint(byte[] bytes) {
        if (isValidUTF8(bytes)) {
            int countBytes = bytes.length;
            int result = 0;

            if (countBytes == 1) {
                System.out.println("1 bytes");
                return bytes[0];

            } else if (countBytes == 2) {
                System.out.println("2 bytes");

                int main = bytes[0] ^ (-64);
                main = main << 6;

                int followUp1 = bytes[1] ^ (-128);

                return main + followUp1;

            } else if (countBytes == 3) {
                System.out.println("3 bytes");

                int main = bytes[0] ^ -32;
                main = main << 12;
                int followUp1 = bytes[1] ^ -128;
                followUp1 = followUp1 << 6;

                int followUp2 = bytes[2] ^ -128;

                return main + followUp1 + followUp2;
            } else if (countBytes == 4) {
                System.out.println("4 bytes");

                int main = bytes[0] ^ -16;
                main = main << 18;

                int followUp1 = bytes[1] ^ -128;
                followUp1 = followUp1 << 12;

                int followUp2 = bytes[2] ^ -128;
                followUp2 = followUp2 << 6;

                int followUp3 = bytes[3] ^ -128;

                return main + followUp1 + followUp2 + followUp3;
            }

            return 0;
        } else {
            return 0;
        }
    }

    private static boolean isValidUTF8(byte[] bytes) {
        if (bytes.length == 1) return (bytes[0] & 0b1000_0000) == 0;
        else if (bytes.length == 2) return ((bytes[0] & 0b1110_0000) == 0b1100_0000) && isFollowup(bytes[1]);
        else if (bytes.length == 3)
            return ((bytes[0] & 0b1111_0000) == 0b1110_0000) && isFollowup(bytes[1]) && isFollowup(bytes[2]);
        else if (bytes.length == 4)
            return ((bytes[0] & 0b1111_1000) == 0b1111_0000) && isFollowup(bytes[1]) && isFollowup(bytes[2]) && isFollowup(bytes[3]);
        else return false;
    }

    private static boolean isFollowup(byte b) {
        return (b & 0b1100_0000) == 0b1000_0000;
    }

    private static int binaryStringToInt(String binary) {
        int x = 0;
        for (int i = 0; i < binary.length(); i++) {
            x = (x << 1) + (binary.charAt(i) == '0' ? 0 : 1);
        }

        return x;
    }

    private static String intToBinaryString(int x) {
        String s = "";

        if (x < 0) {
            x = (1 << 8) - (~(x) + 1);
        }

        while (x > 0) {
            if (x % 2 == 1) {
                s = "1" + s;
            } else {
                s = "0" + s;
            }
            x = x >> 1;
        }

        return s;
    }
}
