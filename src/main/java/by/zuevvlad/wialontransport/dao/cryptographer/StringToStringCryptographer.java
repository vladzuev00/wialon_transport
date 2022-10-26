package by.zuevvlad.wialontransport.dao.cryptographer;

//TODO: md5
public final class StringToStringCryptographer implements Cryptographer<String, String> {
    private StringToStringCryptographer() {

    }

    public static Cryptographer<String, String> create() {
        return SingletonHolder.CRYPTOGRAPHER;
    }

    @Override
    public String encrypt(String data) {
        return null;
    }

    @Override
    public String decrypt(String data) {
        return null;
    }

    private static final class SingletonHolder {
        private static final Cryptographer<String, String> CRYPTOGRAPHER = new StringToStringCryptographer();
    }
}
