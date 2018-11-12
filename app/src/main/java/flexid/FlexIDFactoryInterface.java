package flexid;

/**
 * This class defines the interface to manage Flex IDs
 * @author Hyunwoo Lee
 */

public interface FlexIDFactoryInterface {

    /**
     * This function generates the new Flex ID with regard to the interface inputted
     * @param peer the peer's Flex ID to get the appropriate Flex ID
     * @return a newly generated Flex ID
     */
    FlexID getMyFlexID(FlexID peer);

    /**
     * This function generates the peer's Flex ID regarding the peer's ID and the locator
     * The entity should search for the information about the peer before invoking this function
     * @param loc the locator of the peer
     * @param
     * @return
     */
    FlexID setPeerFlexID(Locator loc, AttrValuePairs avps);
}
