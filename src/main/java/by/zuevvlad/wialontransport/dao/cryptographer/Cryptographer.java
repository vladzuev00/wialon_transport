package by.zuevvlad.wialontransport.dao.cryptographer;

public interface Cryptographer<ToBeEncryptedDataType, ToBeDecryptedDataType>
{
    ToBeDecryptedDataType encrypt(final ToBeEncryptedDataType data);
    ToBeEncryptedDataType decrypt(final ToBeDecryptedDataType data);
}
