import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * @author Anand McCoole, Dartmouth CS 10, 01/29/2022, TA: Jason Pak
 * @author Ryan Kim, Dartmouth CS 10, 01/29/2022, TA: Caroline Hall
 *
 */

public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 * @param p2, point being inserted
	 */
	public void insert(E p2) {
		// Getting the x,y values of point for easier usage
		double px = p2.getX();
		double py = p2.getY();

		/*
		Recursively checks and inserts p2 into correct spot based on its coordinates, quadrants, and the child nodes
		- If p2 is in noted quadrant, create new child node/tree or call insert on existing node
		 */
		// For quadrant 1
		if (px >= point.getX() && py <= point.getY()) {
			if (hasChild(1)) { c1.insert(p2); }
			else { c1 = new PointQuadtree<E>(p2, (int) point.getX(), getY1(), getX2(), (int) point.getY()); }
		}
		// For quadrant 2
		if (px <= point.getX() && py <= point.getY()) {
			if (hasChild(2)) { c2.insert(p2); }
			else { c2 = new PointQuadtree<E>(p2, getX1(), getY1(), (int)point.getX(), (int)point.getY()); }
		}
		// For quadrant 3
		if (px <= point.getX() && py >= point.getY()) {
			if (hasChild(3)) { c3.insert(p2); }
			else { c3 = new PointQuadtree<E>(p2, getX1(), (int)point.getY(), (int)point.getX(), getY2()); }
		}
		// For quadrant 4
		if (px >= point.getX() && py >= point.getY()) {
			if (hasChild(4)) { c4.insert(p2); }
			else { c4 = new PointQuadtree<E>(p2, (int)point.getX(), (int)point.getY(), getX2(), getY2()); }
		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int sum = 1;

		if (!hasChild(1) && !hasChild(2) && !hasChild(3) && !hasChild(4)) {
			return 1;
		}
		if (hasChild(1)) { sum += c1.size(); }
		if (hasChild(2)) { sum += c2.size(); }
		if (hasChild(3)) { sum += c3.size(); }
		if (hasChild(4)) { sum += c4.size(); }

		return sum;
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 * @return pointList, a list of all the points
	 */
	public List<E> allPoints() {
		ArrayList<E> pointList = new ArrayList<>();

		// Adds current point to list
		pointList.add(point);

		// Base Case – if point has no children, return existing pointList (holds nothing)
		if (!hasChild(1) && !hasChild(2) && !hasChild(3) && !hasChild(4)) {
			return pointList;
		}

		// If there are children in any quadrant, call allPoints() on these children
		// which will return list of points each child has and be added to greater list of points
		if (hasChild(1)) { pointList.addAll(c1.allPoints()); }
		if (hasChild(2)) { pointList.addAll(c2.allPoints()); }
		if (hasChild(3)) { pointList.addAll(c3.allPoints()); }
		if (hasChild(4)) { pointList.addAll(c4.allPoints()); }

		return pointList;
	}

	/**
	 * Uses the quadtree to find all points within the circle (uses helper function circlePointAccumulator)
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		ArrayList<E> circlePoints = new ArrayList<>();
		circlePointAccumulator(circlePoints, cx, cy, cr);
		return circlePoints;
	}

	/**
	 * Uses methods provided in Geometry.java to accumulate all points in circle into list points
	 * @param points list of points within circle
	 * @param cx x value of circle
	 * @param cy y value of circle
	 * @param cr radius of circle
	 */
	public void circlePointAccumulator(ArrayList<E> points, double cx, double cy, double cr) {

		if (!Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {return; }

		if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) { points.add(point); }

		if (hasChild(1)) { c1.circlePointAccumulator(points, cx, cy, cr); }
		if (hasChild(2)) { c2.circlePointAccumulator(points, cx, cy, cr); }
		if (hasChild(3)) { c3.circlePointAccumulator(points, cx, cy, cr); }
		if (hasChild(4)) { c4.circlePointAccumulator(points, cx, cy, cr); }
	}
}
