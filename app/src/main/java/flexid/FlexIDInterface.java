package flexid;

public interface FlexIDInterface {

    /**
     * Get the Identity of the Flex ID
     * @return byte array representation of the Flex ID
     */
    byte[] getIdentity();

    /**
     * Get the value from the AVPs of the Flex ID with regard to the attribute
     * @param attr the attribute
     * @return
     */
    String getValueByAttr(String attr);

    /**
     * Get the current locator of the Flex ID
     * @return the current locator
     */
    Locator getLocator();

    /**
     * Update the locator mapping of the Flex ID
     * @param loc the locator information
     */
    void updateLocator(Locator loc);
}
