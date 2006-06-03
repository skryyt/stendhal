/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.pathfinder;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;

import java.util.LinkedList;
import java.util.List;

public class PathfinderTest {
	public static void main(String[] args) throws Exception {
		int x0 = 0;
		int y0 = 0;
		int x1 = 0;
		int y1 = 0;

		StendhalRPWorld world = new StendhalRPWorld();
		world.addArea("-1_semos_dungeon");

		StendhalRPZone zone = (StendhalRPZone) world
				.getRPZone("-1_semos_dungeon");

		if (args.length < 4) {
			do {
				x0 = Rand.rand(64);
				y0 = Rand.rand(64);
			} while (zone.collides(x0, y0));

			do {
				x1 = Rand.rand(64);
				y1 = Rand.rand(64);
			} while (zone.collides(x1, y1));
		} else {
			x0 = Integer.parseInt(args[0]);
			y0 = Integer.parseInt(args[1]);
			x1 = Integer.parseInt(args[2]);
			y1 = Integer.parseInt(args[3]);
		}

		System.out.println(x0 + "," + y0 + " to " + x1 + "," + y1);

		long startTime = System.currentTimeMillis();

		List<Path.Node> nodes = null;

		for (int i = 0; i < 10000; i++) {
			// nodes=box.getPath(x0,y0,x1,y1);
			nodes = searchAstartPath(zone, null, x0, y0, x1, y1);
		}

		long endTime = System.currentTimeMillis();

		for (int j = 0; j < zone.getHeight(); j++) {
			for (int i = 0; i < zone.getWidth(); i++) {
				boolean contained = false;

				for (Path.Node node : nodes) {
					if (node.x == i && node.y == j) {
						contained = true;
						break;
					}
				}

				if (contained) {
					System.out.print("P");
				} else if (x0 == i && y0 == j) {
					System.out.print("O");
				} else if (x1 == i && y1 == j) {
					System.out.print("D");
				} else if (zone.collides(i, j)) {
					System.out.print("*");
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}

		System.out.println("STATUS: " + null + "\t TIME: "
				+ (endTime - startTime));
	}

	static public class GraphNavigable implements Navigable {
		Graph g;

		int x;

		int y;

		StendhalRPZone zone;

		public GraphNavigable(StendhalRPZone zone, Graph g, int x, int y) {
			this.zone = zone;
			this.g = g;
			this.x = x;
			this.y = y;
		}

		public boolean isValid(Pathfinder.Node node) {
			return !zone.collides(node.x, node.y);
		}

		public double getCost(Pathfinder.Node parent, Pathfinder.Node child) {
			int dx = parent.getX() - child.getX();
			int dy = parent.getY() - child.getY();

			return (dx * dx) + (dy * dy);
		}

		public double getHeuristic(Pathfinder.Node parent, Pathfinder.Node child) {
			int dx = parent.getX() - child.getX();
			int dy = parent.getY() - child.getY();

			return (dx * dx) + (dy * dy);
		}

		public boolean reachedGoal(Pathfinder.Node nodeBest) {
			return nodeBest.getX() == x && nodeBest.getY() == y;
		}

		public int createNodeID(Pathfinder.Node node) {
			return node.x + node.y * zone.getWidth();
		}

		public void createChildren(Pathfinder path, Pathfinder.Node node) {
			int x = node.x, y = node.y;
			Pathfinder.Node tempNode = new Pathfinder.Node();

			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					tempNode.x = x + i;
					tempNode.y = y + j;
					// If the node is this node, or invalid continue.
					if ((i == 0 && j == 0) || (Math.abs(i) == Math.abs(j))
							|| isValid(tempNode) == false)
						continue;

					path.linkChild(node, x + i, y + j);
				}
			}
		}
	}

	public static List<Path.Node> searchAstartPath(StendhalRPZone zone,
			Graph g, int x0, int y0, int x1, int y1) {
		Pathfinder path = new Pathfinder();

		Navigable navMap = new GraphNavigable(zone, g, x1, y1);
		path.setNavigable(navMap);
		path.setStart(new Pathfinder.Node(x0, y0));
		path.setGoal(new Pathfinder.Node(x1, y1));

		path.init();
		while (path.getStatus() == Pathfinder.IN_PROGRESS) {
			path.doStep();
		}

		List<Path.Node> list = new LinkedList<Path.Node>();
		Pathfinder.Node node = path.getBestNode();
		while (node != null) {
			list.add(0, new Path.Node(node.getX(), node.getY()));
			node = node.getParent();
		}

		return list;
	}
}
