/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package jade.core;


//#MIDP_EXCLUDE_BEGIN
import jade.domain.AMSEventQueueFeeder;
//#MIDP_EXCLUDE_END
import jade.lang.acl.ACLMessage;

import jade.util.leap.List;

import jade.security.JADESecurityException;
import jade.security.JADEPrincipal;
import jade.security.Credentials;


/**
 * This interface represents the local container as it is seen by JADE kernel level 
 * services installed in the underlying Node
 * @author Giovanni Rimassa - Universita' di Parma
 * @version $Date: 2007-06-20 15:38:43 +0200 (mer, 20 giu 2007) $ $Revision: 5981 $
 */

public interface AgentContainer {
	static final String MAIN_CONTAINER_NAME = "Main-Container";
	static final String AUX_CONTAINER_NAME = "Container";

	AID getAMS();
	AID getDefaultDF();

	ContainerID getID();
	String getPlatformID();
	MainContainer getMain();
	ServiceFinder getServiceFinder();
	//#APIDOC_EXCLUDE_BEGIN
	ServiceManager getServiceManager();
	NodeDescriptor getNodeDescriptor();

	void initAgent(AID agentID, Agent instance,  
			JADEPrincipal ownerPrincipal, Credentials initialCredentials                  
	) throws NameClashException, IMTPException, NotFoundException, JADESecurityException;
	void powerUpLocalAgent(AID agentID) throws NotFoundException;
	Agent addLocalAgent(AID id, Agent a);
	void removeLocalAgent(AID id);
	//#APIDOC_EXCLUDE_END
	Agent acquireLocalAgent(AID id);
	void releaseLocalAgent(AID id);
	AID[] agentNames();

	//#APIDOC_EXCLUDE_BEGIN
	//#MIDP_EXCLUDE_BEGIN
	void fillListFromMessageQueue(List messages, Agent a);
	void fillListFromReadyBehaviours(List behaviours, Agent a);
	void fillListFromBlockedBehaviours(List behaviours, Agent a);

	// GC-MODIFY-18022007-START
	void becomeLeader(AMSEventQueueFeeder feeder);
	// GC-MODIFY-18022007-END
	//#MIDP_EXCLUDE_END

	void addAddressToLocalAgents(String address);
	void removeAddressFromLocalAgents(String address);
	boolean postMessageToLocalAgent(ACLMessage msg, AID receiverID);
	boolean livesHere(AID id);
	Location here();

	void shutDown();
	//#APIDOC_EXCLUDE_END
}
