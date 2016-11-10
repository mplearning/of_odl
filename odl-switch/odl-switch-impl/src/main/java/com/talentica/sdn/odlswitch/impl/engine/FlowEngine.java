/**
 * 
 */
package com.talentica.sdn.odlswitch.impl.engine;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetQueueActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetTpDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetTpSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.dst.action._case.SetDlDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.src.action._case.SetDlSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.dst.action._case.SetNwDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.src.action._case.SetNwSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.queue.action._case.SetQueueActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.tp.dst.action._case.SetTpDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.tp.src.action._case.SetTpSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.address.address.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.talentica.sdn.odlcommon.odlutils.exception.OdlDataStoreException;
import com.talentica.sdn.odlcommon.odlutils.utils.CommonUtils;
import com.talentica.sdn.odlcommon.odlutils.utils.Constants;
import com.talentica.sdn.odlcommon.odlutils.utils.FlowUtils;

/**
 * * @author narenderK
 *
 */
public class FlowEngine {
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 * @param dataBroker
	 * @param nodeId
	 * @param srcMac
	 * @param dstMac
	 * @param ingressNodeConnectorId
	 * @param role
	 * @param dstPort
	 * @throws OdlDataStoreException
	 */
	public static void programL2Flow(DataBroker dataBroker, NodeId nodeId, String srcMac, String dstMac, String role) throws OdlDataStoreException {
		MatchBuilder matchBuilder = new MatchBuilder();
		CommonUtils.createEthMatch(matchBuilder, new MacAddress(srcMac), new MacAddress(dstMac), null);
		
		ActionBuilder ab = new ActionBuilder();
		List<Action> actionList = new ArrayList<>();

		// Set output action
		OutputActionBuilder output = new OutputActionBuilder();
		Uri value = new Uri(Constants.OPENFLOW_FORWARDING_ACTION_NORMAL);
	    output.setOutputNodeConnector(value);
		output.setMaxLength(65535);
		ab.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
		ab.setOrder(1);
		actionList.add(ab.build());
		
		ActionBuilder ab2 = new ActionBuilder();
		SetQueueActionBuilder setQueueActionBuilder = new SetQueueActionBuilder();
		if(role.equalsIgnoreCase(Constants.ROLE_EMPLOYEE)){
			setQueueActionBuilder.setQueueId(1L);
		}else{
			setQueueActionBuilder.setQueueId(2L);
		}
        ab2.setAction(new SetQueueActionCaseBuilder().setSetQueueAction(setQueueActionBuilder.build()).build());
        ab2.setOrder(0);
        actionList.add(ab2.build());

		// Create Apply Actions Instruction
		InstructionsBuilder isb = new InstructionsBuilder();
		List<Instruction> instructions = Lists.newArrayList();
		InstructionBuilder ib = new InstructionBuilder();
		ApplyActionsBuilder aab = new ApplyActionsBuilder();
		
		aab.setAction(actionList);
		ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
		ib.setOrder(0);
		ib.setKey(new InstructionKey(0));
		instructions.add(ib.build());

		// Create Flow
		FlowBuilder flowBuilder = new FlowBuilder();
		String flowId = "L2_Rule_" + srcMac +"_to_" +dstMac;
		FlowUtils.createFlowBuilder(flowBuilder, matchBuilder,flowId, 12);
		flowBuilder.setInstructions(isb.setInstruction(instructions).build());

		InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
				.child(Node.class, new NodeKey(nodeId)).augmentation(FlowCapableNode.class)
				.child(Table.class, new TableKey(flowBuilder.getTableId())).child(Flow.class, flowBuilder.getKey())
				.build();
		CommonUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(),
				true);
	}
	
	/**
	 * 
	 * @param dataBroker
	 * @param ingressNodeConnectorId
	 * @param srcMac
	 * @param dstMac
	 * @param srcIp
	 * @param dstIp
	 * @param dstPort
	 * @throws OdlDataStoreException
	 */
	public static void addforwardflow(DataBroker dataBroker, NodeConnectorId ingressNodeConnectorId, String srcMac, String dstMac, String srcIp, String dstIp,
			int dstPort) throws OdlDataStoreException {
		MatchBuilder matchBuilder = new MatchBuilder();
		CommonUtils.createEthMatch(matchBuilder, new MacAddress(srcMac), new MacAddress(dstMac), null);
		Ipv4Prefix srcip = new Ipv4Prefix(srcIp + "/32");
		Ipv4Prefix dstip = new Ipv4Prefix(dstIp + "/32");
		CommonUtils.createL3IPv4Match(matchBuilder, srcip, dstip);
		CommonUtils.createSetTcpDstMatch(matchBuilder, new PortNumber(dstPort));
		List<Action> actionList = new ArrayList<>();
		
		// Set output action
		ActionBuilder ab1 = new ActionBuilder();
		OutputActionBuilder output = new OutputActionBuilder();
		Uri value = new Uri(Constants.OPENFLOW_FORWARDING_ACTION_NORMAL);
	    output.setOutputNodeConnector(value);
		output.setMaxLength(65535);
		ab1.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
		ab1.setOrder(3);
		actionList.add(ab1.build());
		
		// Set dl dst action
		ActionBuilder ab2 = new ActionBuilder();
		SetDlDstActionBuilder dl = new SetDlDstActionBuilder();
        dl.setAddress(new MacAddress(Constants.CAPTIVE_PORTAL_MAC));
		ab2.setAction(new SetDlDstActionCaseBuilder().setSetDlDstAction(dl.build()).build());
		ab2.setOrder(0);
		actionList.add(ab2.build());
		
		// Set new dst action
		ActionBuilder ab3 = new ActionBuilder();
		Ipv4Builder ipDest = new Ipv4Builder();
		Ipv4Address ipAddress = new Ipv4Address(Constants.CAPTIVE_PORTAL_IP);
		Ipv4Prefix prefixdst = new Ipv4Prefix(new Ipv4Prefix(ipAddress.getValue() + "/32"));
		ipDest.setIpv4Address(prefixdst);
		SetNwDstActionBuilder setNwDstActionBuilder = new SetNwDstActionBuilder();
		setNwDstActionBuilder.setAddress(ipDest.build());
		ab3.setAction(new SetNwDstActionCaseBuilder().setSetNwDstAction(setNwDstActionBuilder.build()).build());
		ab3.setOrder(1);
		actionList.add(ab3.build());
		
		// Set tcp dst action
		ActionBuilder ab4 = new ActionBuilder();
		SetTpDstActionBuilder tpDstBuilder = new SetTpDstActionBuilder();
		tpDstBuilder.setPort(new PortNumber(Constants.CAPTIVE_PORTAL_SERVER_PORT));
		ab4.setAction(new SetTpDstActionCaseBuilder().setSetTpDstAction(tpDstBuilder.build()).build());
		ab4.setOrder(2);
		actionList.add(ab4.build());

		// Create Apply Actions Instruction
		InstructionsBuilder isb = new InstructionsBuilder();
		List<Instruction> instructions = Lists.newArrayList();
		InstructionBuilder ib = new InstructionBuilder();
		ApplyActionsBuilder aab = new ApplyActionsBuilder();
		aab.setAction(actionList);
		ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
		ib.setOrder(0);
		ib.setKey(new InstructionKey(0));
		instructions.add(ib.build());

		// Create Flow
		FlowBuilder flowBuilder = new FlowBuilder();
		String flowId = "ForwardFlow_"+srcMac;
		FlowUtils.createFlowBuilder(flowBuilder, matchBuilder,flowId, 13);
		flowBuilder.setInstructions(isb.setInstruction(instructions).build());

		InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
				.child(Node.class, new NodeKey(CommonUtils.getNodeId(ingressNodeConnectorId))).augmentation(FlowCapableNode.class)
				.child(Table.class, new TableKey(flowBuilder.getTableId())).child(Flow.class, flowBuilder.getKey())
				.build();
		CommonUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(), true);
	}
	
	/**
	 * 
	 * @param dataBroker
	 * @param ingressNodeConnectorId
	 * @param srcMac
	 * @param dstMac
	 * @param srcIp
	 * @param dstIp
	 * @param dstPort
	 * @throws OdlDataStoreException
	 */
	public static void addReverseflow(DataBroker dataBroker, NodeConnectorId ingressNodeConnectorId, String srcMac, String dstMac, String srcIp, String dstIp, int dstPort) throws OdlDataStoreException {
		MatchBuilder matchBuilder = new MatchBuilder();
		CommonUtils.createEthMatch(matchBuilder, new MacAddress(Constants.CAPTIVE_PORTAL_MAC),new MacAddress(srcMac), null);
		Ipv4Prefix srcip = new Ipv4Prefix(Constants.CAPTIVE_PORTAL_IP + "/32");
		Ipv4Prefix dstip = new Ipv4Prefix(srcIp + "/32");
		CommonUtils.createL3IPv4Match(matchBuilder, srcip, dstip);
		CommonUtils.createSetTcpSrcMatch(matchBuilder, new PortNumber(Constants.CAPTIVE_PORTAL_SERVER_PORT));
		List<Action> actionList1 = new ArrayList<>();
		
		// Set output action
		ActionBuilder ab1 = new ActionBuilder();
		OutputActionBuilder output = new OutputActionBuilder();
		Uri value = new Uri(Constants.OPENFLOW_FORWARDING_ACTION_NORMAL);
	    output.setOutputNodeConnector(value);
		output.setMaxLength(65535);
		ab1.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
		ab1.setOrder(3);
		actionList1.add(ab1.build());

		// Set dl src action
		ActionBuilder ab2 = new ActionBuilder();
		SetDlSrcActionBuilder dl = new SetDlSrcActionBuilder();
		dl.setAddress(new MacAddress(dstMac));
		ab2.setAction(new SetDlSrcActionCaseBuilder().setSetDlSrcAction(dl.build()).build());
		ab2.setOrder(0);
		actionList1.add(ab2.build());

		// Set new src action
		ActionBuilder ab3 = new ActionBuilder();
		Ipv4Builder ipSrc = new Ipv4Builder();
		Ipv4Address ipAddress = new Ipv4Address(dstIp);
		Ipv4Prefix prefixdst = new Ipv4Prefix(new Ipv4Prefix(ipAddress.getValue() + "/32"));
		ipSrc.setIpv4Address(prefixdst);
		SetNwSrcActionBuilder setNwSrcActionBuilder = new SetNwSrcActionBuilder();
		setNwSrcActionBuilder.setAddress(ipSrc.build());
		ab3.setAction(new SetNwSrcActionCaseBuilder().setSetNwSrcAction(setNwSrcActionBuilder.build()).build());
		ab3.setOrder(1);
		actionList1.add(ab3.build());

		// Set tcp src action
		ActionBuilder ab4 = new ActionBuilder();
		SetTpSrcActionBuilder tpSrcBuilder = new SetTpSrcActionBuilder();
		tpSrcBuilder.setPort(new PortNumber(dstPort));
		ab4.setAction(new SetTpSrcActionCaseBuilder().setSetTpSrcAction(tpSrcBuilder.build()).build());
		ab4.setOrder(2);
		actionList1.add(ab4.build());

		// Create Apply Actions Instruction
		InstructionsBuilder isb = new InstructionsBuilder();
		List<Instruction> instructions = Lists.newArrayList();
		InstructionBuilder ib = new InstructionBuilder();
		ApplyActionsBuilder aab = new ApplyActionsBuilder();
		aab.setAction(actionList1);
		ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
		ib.setOrder(0);
		ib.setKey(new InstructionKey(0));
		instructions.add(ib.build());

		// Create Flow
		FlowBuilder flowBuilder = new FlowBuilder();
		String flowId = "ReverseFlow_"+srcMac;
		FlowUtils.createFlowBuilder(flowBuilder, matchBuilder,flowId,13);
		flowBuilder.setInstructions(isb.setInstruction(instructions).build());

		InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
				.child(Node.class, new NodeKey(CommonUtils.getNodeId(ingressNodeConnectorId)))
				.augmentation(FlowCapableNode.class).child(Table.class, new TableKey(flowBuilder.getTableId()))
				.child(Flow.class, flowBuilder.getKey()).build();
		CommonUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(), true);
	}
	
	/**
	 * 
	 * @param dataBroker
	 * @param nodeId
	 * @throws OdlDataStoreException
	 */
	public static void programFloodARPFlow(DataBroker dataBroker, NodeId nodeId) throws OdlDataStoreException {
		MatchBuilder matchBuilder = new MatchBuilder();
		CommonUtils.createEthTypeARPMatch(matchBuilder);
		
		ActionBuilder ab = new ActionBuilder();
		List<Action> actionList = Lists.newArrayList();

		// Set output action
		OutputActionBuilder output = new OutputActionBuilder();
		Uri value = new Uri(Constants.OPENFLOW_FORWARDING_ACTION_FLOOD);
	    output.setOutputNodeConnector(value);
		output.setMaxLength(65535);
		ab.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
		ab.setOrder(0);
		ab.setKey(new ActionKey(0));
		actionList.add(ab.build());

		// Create Apply Actions Instruction
		InstructionsBuilder isb = new InstructionsBuilder();
		List<Instruction> instructions = Lists.newArrayList();
		InstructionBuilder ib = new InstructionBuilder();
		ApplyActionsBuilder aab = new ApplyActionsBuilder();
		
		aab.setAction(actionList);
		ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
		ib.setOrder(0);
		ib.setKey(new InstructionKey(0));
		instructions.add(ib.build());

		// Create Flow
		FlowBuilder flowBuilder = new FlowBuilder();
		String flowId = "FLOOD_ARP_" + nodeId.getValue();
		FlowUtils.createFlowBuilder(flowBuilder, matchBuilder,flowId,2);		
		flowBuilder.setInstructions(isb.setInstruction(instructions).build());

		InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
				.child(Node.class, new NodeKey(nodeId)).augmentation(FlowCapableNode.class)
				.child(Table.class, new TableKey(flowBuilder.getTableId())).child(Flow.class, flowBuilder.getKey())
				.build();
		CommonUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(),
				true);
		
	}
	
}
