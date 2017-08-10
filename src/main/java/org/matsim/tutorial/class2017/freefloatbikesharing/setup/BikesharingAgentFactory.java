/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.tutorial.class2017.freefloatbikesharing.setup;

import java.util.Map;

import javax.inject.Inject;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.contrib.parking.parkingsearch.DynAgent.agentLogic.ParkingAgentLogic;
import org.matsim.contrib.parking.parkingsearch.manager.ParkingSearchManager;
import org.matsim.contrib.parking.parkingsearch.manager.WalkLegFactory;
import org.matsim.contrib.parking.parkingsearch.manager.vehicleteleportationlogic.VehicleTeleportationLogic;
import org.matsim.contrib.parking.parkingsearch.routing.ParkingRouter;
import org.matsim.contrib.parking.parkingsearch.search.ParkingSearchLogic;
import org.matsim.contrib.parking.parkingsearch.search.RandomParkingSearchLogic;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.tutorial.class2017.freefloatbikesharing.BikesharingAgentLogic;

/**
 * @author jbischoff
 *
 */

public class BikesharingAgentFactory implements AgentFactory {

	/**
	 * 
	 */
	@Inject
	WalkLegFactory walkLegFactory;
	
	@Inject
	EventsManager events;
	Network network;
	
	@Inject
	BikeSharingManager manager;
	@Inject
	Map<String, TravelTime> travelTimes;
	private final QSim qsim;
	private LeastCostPathCalculator lcp;
	@Inject
	Map<String, TravelDisutilityFactory> travelDisutilityFactories;
	/**
	 * 
	 */
	@Inject
	public BikesharingAgentFactory(QSim qsim, Network network, LeastCostPathCalculatorFactory df) {
		this.network = network;
		this.qsim = qsim;
		FreespeedTravelTimeAndDisutility freespeedTravelTimeAndDisutility = new FreespeedTravelTimeAndDisutility(0, 0, 0);
		TravelDisutility carDis = new OnlyTimeDependentTravelDisutility(freespeedTravelTimeAndDisutility);
		lcp = df.createPathCalculator(network, carDis, freespeedTravelTimeAndDisutility);
	}

	@Override
	public MobsimAgent createMobsimAgentFromPerson(Person p) {
		
		
		Id<Link> startLinkId = ((Activity) p.getSelectedPlan().getPlanElements().get(0)).getLinkId();
		if (startLinkId == null) {
			throw new NullPointerException(" No start link found. Should not happen.");
		}
		DynAgent agent = new DynAgent(p.getId(), startLinkId, events, new BikesharingAgentLogic(p.getSelectedPlan(),walkLegFactory,events,qsim.getSimTimer(),manager, network, lcp));
		return agent;
	}

}
