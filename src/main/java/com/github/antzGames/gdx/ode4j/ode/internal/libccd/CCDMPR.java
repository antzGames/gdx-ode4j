/***
 * libccd
 * ---------------------------------
 * Copyright (c)2010,2011 Daniel Fiser <danfis@danfis.cz>
 * Java-port: Copyright (c) 2009-2014 Tilmann Zaeschke<ode4j@gmx.de>  
 *
 *
 *  This file is part of libccd.
 *
 *  Distributed under the OSI-approved BSD License (the "License");
 *  see accompanying file BDS-LICENSE for details or see
 *  <http://www.opensource.org/licenses/bsd-license.php>.
 *
 *  This software is distributed WITHOUT ANY WARRANTY; without even the
 *  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the License for more information.
 */
package com.github.antzGames.gdx.ode4j.ode.internal.libccd;

import com.github.antzGames.gdx.ode4j.ode.internal.cpp4j.java.RefDouble;
import com.github.antzGames.gdx.ode4j.ode.internal.libccd.CCD.ccd_t;

import static com.github.antzGames.gdx.ode4j.ode.internal.libccd.CCDSimplex.*;

/**
 *
 * LibCCD MPR collider.
 */
public class CCDMPR {

	/**
	 * Returns true if two given objects intersect - MPR algorithm is used.
	 * @param obj1 object 1
	 * @param obj2 object 2
	 * @param ccd ccd
	 * @return 1 of objects intersect otherwise 0.
	 */
	public static int ccdMPRIntersect(final Object obj1, final Object obj2, final ccd_t ccd)
	{
	    ccd_simplex_t portal = new ccd_simplex_t();
	    int res;

	    // Phase 1: Portal discovery - find portal that intersects with origin
	    // ray (ray from center of Minkowski diff to origin of coordinates)
	    res = discoverPortal(obj1, obj2, ccd, portal);
	    if (res < 0)
	        return 0;
	    if (res > 0)
	        return 1;

	    // Phase 2: Portal refinement
	    res = refinePortal(obj1, obj2, ccd, portal);
	    return (res == 0 ? 1 : 0);
	}

	/**
	 * Computes penetration of obj2 into obj1.
	 * Depth of penetration, direction and position is returned, i.e. if obj2
	 * is translated by computed depth in resulting direction obj1 and obj2
	 * would have touching contact. Position is point in global coordinates
	 * where force should be take a place.
	 *
	 * Minkowski Portal Refinement algorithm is used (MPR, a.k.a. XenoCollide,
	 * see Game Programming Gem 7).
	 *
	 * Returns 0 if obj1 and obj2 intersect, otherwise -1 is returned.
	 * @param obj1 object 1
	 * @param obj2 object 2
	 * @param ccd ccd
	 * @param depth resulting penetration depth.
	 * @param dir direction
	 * @param pos position
	 * @return -1 if objects do not intersect, otherwise 0.
	 */
	public static int ccdMPRPenetration(final Object obj1, final Object obj2, final ccd_t ccd,
                                        RefDouble depth, CCDVec3.ccd_vec3_t dir, CCDVec3.ccd_vec3_t pos)
	{
	    ccd_simplex_t portal = new ccd_simplex_t();
	    int res;

	    // Phase 1: Portal discovery
	    res = discoverPortal(obj1, obj2, ccd, portal);
	    if (res < 0){
	        // Origin isn't inside portal - no collision.
	        return -1;

	    }else if (res == 1){
	        // Touching contact on portal's v1.
	        findPenetrTouch(obj1, obj2, ccd, portal, depth, dir, pos);

	    }else if (res == 2){
	        // Origin lies on v0-v1 segment.
	        findPenetrSegment(obj1, obj2, ccd, portal, depth, dir, pos);

	    }else if (res == 0){
	        // Phase 2: Portal refinement
	        res = refinePortal(obj1, obj2, ccd, portal);
	        if (res < 0)
	            return -1;

	        // Phase 3. Penetration info
	        findPenetr(obj1, obj2, ccd, portal, depth, dir, pos);
	    }

	    return 0;
	}



	/** Finds origin (center) of Minkowski difference (actually it can be any
	 *  interior point of Minkowski difference. */
	private static final void findOrigin(final Object obj1, final Object obj2, final ccd_t ccd,
	                            CCDSupport.ccd_support_t center)
	{
	    ccd.center1.run(obj1, center.v1);
	    ccd.center2.run(obj2, center.v2);
	    CCDVec3.ccdVec3Sub2(center.v, center.v1, center.v2);
	}

	/** Discovers initial portal - that is tetrahedron that intersects with
	 *  origin ray (ray from center of Minkowski diff to (0,0,0).
	 *
	 *  Returns -1 if already recognized that origin is outside Minkowski
	 *  portal.
	 *  Returns 1 if origin lies on v1 of simplex (only v0 and v1 are present
	 *  in simplex).
	 *  Returns 2 if origin lies on v0-v1 segment.
	 *  Returns 0 if portal was built.
	 */
	private static int discoverPortal(final Object obj1, final Object obj2,
	                          final ccd_t ccd, ccd_simplex_t portal)
	{
	    final CCDVec3.ccd_vec3_t dir = new CCDVec3.ccd_vec3_t(), va = new CCDVec3.ccd_vec3_t(), vb = new CCDVec3.ccd_vec3_t();
	    double dot;
	    int cont;

	    // vertex 0 is center of portal
	    findOrigin(obj1, obj2, ccd, ccdSimplexPointW0(portal));
	    ccdSimplexSetSize(portal,1);

	    if (CCDVec3.ccdVec3Eq(ccdSimplexPoint0(portal).v, CCDVec3.ccd_vec3_origin)){
	        // Portal's center lies on origin (0,0,0) => we know that objects
	        // intersect but we would need to know penetration info.
	        // So move center little bit...
	        CCDVec3.ccdVec3Set(va, CCDVec3.CCD_EPS * (10.), CCDVec3.CCD_ZERO, CCDVec3.CCD_ZERO);
	        CCDVec3.ccdVec3Add(ccdSimplexPointW0(portal).v, va);
	    }


	    // vertex 1 = support in direction of origin
	    CCDVec3.ccdVec3Copy(dir, ccdSimplexPoint0(portal).v);
	    CCDVec3.ccdVec3Scale(dir, (-1.));
	    CCDVec3.ccdVec3Normalize(dir);
	    CCDSupport.__ccdSupport(obj1, obj2, dir, ccd, ccdSimplexPointW1(portal));
	    ccdSimplexSetSize(portal,2);

	    // test if origin isn't outside of v1
	    dot = CCDVec3.ccdVec3Dot(ccdSimplexPoint1(portal).v, dir);
	    if (CCDVec3.ccdIsZero(dot) || dot < CCDVec3.CCD_ZERO)
	        return -1;


	    // vertex 2
	    CCDVec3.ccdVec3Cross(dir, ccdSimplexPoint0(portal).v,
	                       ccdSimplexPoint1(portal).v);
	    if (CCDVec3.ccdIsZero(CCDVec3.ccdVec3Len2(dir))){
	        if (CCDVec3.ccdVec3Eq(ccdSimplexPoint1(portal).v, CCDVec3.ccd_vec3_origin)){
	            // origin lies on v1
	            return 1;
	        }else{
	            // origin lies on v0-v1 segment
	            return 2;
	        }
	    }

	    CCDVec3.ccdVec3Normalize(dir);
	    CCDSupport.__ccdSupport(obj1, obj2, dir, ccd, ccdSimplexPointW2(portal));
	    dot = CCDVec3.ccdVec3Dot(ccdSimplexPoint2(portal).v, dir);
	    if (CCDVec3.ccdIsZero(dot) || dot < CCDVec3.CCD_ZERO)
	        return -1;

	    ccdSimplexSetSize(portal, 3);

	    // vertex 3 direction
	    CCDVec3.ccdVec3Sub2(va, ccdSimplexPoint1(portal).v,
	                     ccdSimplexPoint0(portal).v);
	    CCDVec3.ccdVec3Sub2(vb, ccdSimplexPoint2(portal).v,
	                     ccdSimplexPoint0(portal).v);
	    CCDVec3.ccdVec3Cross(dir, va, vb);
	    CCDVec3.ccdVec3Normalize(dir);

	    // it is better to form portal faces to be oriented "outside" origin
	    dot = CCDVec3.ccdVec3Dot(dir, ccdSimplexPoint0(portal).v);
	    if (dot > CCDVec3.CCD_ZERO){
	        ccdSimplexSwap12(portal);//, 1, 2);
	        CCDVec3.ccdVec3Scale(dir, (-1.));
	    }

	    while (ccdSimplexSize(portal) < 4){
	        CCDSupport.__ccdSupport(obj1, obj2, dir, ccd, ccdSimplexPointW3(portal));
	        dot = CCDVec3.ccdVec3Dot(ccdSimplexPoint3(portal).v, dir);
	        if (CCDVec3.ccdIsZero(dot) || dot < CCDVec3.CCD_ZERO)
	            return -1;

	        cont = 0;

	        // test if origin is outside (v1, v0, v3) - set v2 as v3 and
	        // continue
	        CCDVec3.ccdVec3Cross(va, ccdSimplexPoint1(portal).v,
	                          ccdSimplexPoint3(portal).v);
	        dot = CCDVec3.ccdVec3Dot(va, ccdSimplexPoint0(portal).v);
	        if (dot < CCDVec3.CCD_ZERO && !CCDVec3.ccdIsZero(dot)){
	            ccdSimplexSet2(portal, ccdSimplexPoint3(portal));
	            cont = 1;
	        }

	        if (cont==0){
	            // test if origin is outside (v3, v0, v2) - set v1 as v3 and
	            // continue
	            CCDVec3.ccdVec3Cross(va, ccdSimplexPoint3(portal).v,
	                              ccdSimplexPoint2(portal).v);
	            dot = CCDVec3.ccdVec3Dot(va, ccdSimplexPoint0(portal).v);
	            if (dot < CCDVec3.CCD_ZERO && !CCDVec3.ccdIsZero(dot)){
	                ccdSimplexSet1(portal, ccdSimplexPoint3(portal));
	                cont = 1;
	            }
	        }

	        if (cont!=0){
	            CCDVec3.ccdVec3Sub2(va, ccdSimplexPoint1(portal).v,
	                             ccdSimplexPoint0(portal).v);
	            CCDVec3.ccdVec3Sub2(vb, ccdSimplexPoint2(portal).v,
	                             ccdSimplexPoint0(portal).v);
	            CCDVec3.ccdVec3Cross(dir, va, vb);
	            CCDVec3.ccdVec3Normalize(dir);
	        }else{
	            ccdSimplexSetSize(portal, 4);
	        }
	    }

	    return 0;
	}

	/** Expands portal towards origin and determine if objects intersect.
	 *  Already established portal must be given as argument.
	 *  If intersection is found 0 is returned, -1 otherwise */
	private static int refinePortal(final Object obj1, final Object obj2,
	                        final ccd_t ccd, ccd_simplex_t portal)
	{
	    final CCDVec3.ccd_vec3_t dir = new CCDVec3.ccd_vec3_t();
	    final CCDSupport.ccd_support_t v4 = new CCDSupport.ccd_support_t();

	    while (true){
	        // compute direction outside the portal (from v0 throught v1,v2,v3
	        // face)
	        portalDir(portal, dir);

	        // test if origin is inside the portal
	        if (portalEncapsulesOrigin(portal, dir))
	            return 0;

	        // get next support point
	        CCDSupport.__ccdSupport(obj1, obj2, dir, ccd, v4);

	        // test if v4 can expand portal to contain origin and if portal
	        // expanding doesn't reach given tolerance
	        if (!portalCanEncapsuleOrigin(portal, v4, dir)
	                || portalReachTolerance(portal, v4, dir, ccd)){
	            return -1;
	        }

	        // v1-v2-v3 triangle must be rearranged to face outside Minkowski
	        // difference (direction from v0).
	        expandPortal(portal, v4);
	    }

	    //return -1;
	}


	
	/** Finds penetration info by expanding provided portal. */
	private static void findPenetr(final Object obj1, final Object obj2, final ccd_t ccd,
                                   ccd_simplex_t portal,
                                   RefDouble depth, CCDVec3.ccd_vec3_t pdir, CCDVec3.ccd_vec3_t pos)
	{
	    final CCDVec3.ccd_vec3_t dir = new CCDVec3.ccd_vec3_t();
	    final CCDSupport.ccd_support_t v4 = new CCDSupport.ccd_support_t();
	    long iterations;

	    iterations = 0L;
	    while (true){
	        // compute portal direction and obtain next support point
	        portalDir(portal, dir);
	        CCDSupport.__ccdSupport(obj1, obj2, dir, ccd, v4);

	        // reached tolerance . find penetration info
	        if (portalReachTolerance(portal, v4, dir, ccd)
	                || iterations > ccd.max_iterations){
	            depth.set( CCDVec3.ccdVec3PointTriDist2(CCDVec3.ccd_vec3_origin,
	                                          ccdSimplexPoint1(portal).v,
	                                          ccdSimplexPoint2(portal).v,
	                                          ccdSimplexPoint3(portal).v,
	                                          pdir));
	            depth.set( CCDVec3.CCD_SQRT(depth.get()));
	            CCDVec3.ccdVec3Normalize(pdir);

	            // barycentric coordinates:
	            findPos(obj1, obj2, ccd, portal, pos);

	            return;
	        }

	        expandPortal(portal, v4);

	        iterations++;
	    }
	}

	
	/** Finds penetration info if origin lies on portal's v1 */
	private static void findPenetrTouch(final Object obj1, final Object obj2, final ccd_t ccd,
                                        ccd_simplex_t portal,
                                        RefDouble depth, CCDVec3.ccd_vec3_t dir, CCDVec3.ccd_vec3_t pos)
	{
	    // Touching contact on portal's v1 - so depth is zero and direction
	    // is unimportant and pos can be guessed
	    depth.set (0.);
	    CCDVec3.ccdVec3Copy(dir, CCDVec3.ccd_vec3_origin);

	    CCDVec3.ccdVec3Copy(pos, ccdSimplexPoint1(portal).v1);
	    CCDVec3.ccdVec3Add(pos, ccdSimplexPoint1(portal).v2);
	    CCDVec3.ccdVec3Scale(pos, 0.5);
	}

	/** Find penetration info if origin lies on portal's segment v0-v1 */
	private static void findPenetrSegment(final Object obj1, final Object obj2, final ccd_t ccd,
                                          ccd_simplex_t portal,
                                          RefDouble depth, CCDVec3.ccd_vec3_t dir, CCDVec3.ccd_vec3_t pos)
	{
	    /*
	    ccd_vec3_t vec;
	    double k;
	    */

	    // Origin lies on v0-v1 segment.
	    // Depth is distance to v1, direction also and position must be
	    // computed

	    CCDVec3.ccdVec3Copy(pos, ccdSimplexPoint1(portal).v1);
	    CCDVec3.ccdVec3Add(pos, ccdSimplexPoint1(portal).v2);
	    CCDVec3.ccdVec3Scale(pos, (0.5));

	    /*
	    ccdVec3Sub2(vec, ccdSimplexPoint1(portal).v,
	                      ccdSimplexPoint0(portal).v);
	    k  = CCD_SQRT(ccdVec3Len2(ccdSimplexPoint0(portal).v));
	    k /= CCD_SQRT(ccdVec3Len2(vec));
	    ccdVec3Scale(vec, -k);
	    ccdVec3Add(pos, vec);
	    */

	    CCDVec3.ccdVec3Copy(dir, ccdSimplexPoint1(portal).v);
	    depth.set( CCDVec3.CCD_SQRT(CCDVec3.ccdVec3Len2(dir)) );
	    CCDVec3.ccdVec3Normalize(dir);
	}


	
	/** Finds position vector from fully established portal */
	private static void findPos(final Object obj1, final Object obj2, final ccd_t ccd,
			final ccd_simplex_t portal, CCDVec3.ccd_vec3_t pos)
	{
	    final CCDVec3.ccd_vec3_t dir = new CCDVec3.ccd_vec3_t();
	    //int i;
	    double b0, b1, b2, b3;//=new double[4];
	    double sum, inv;
	    final CCDVec3.ccd_vec3_t vec = new CCDVec3.ccd_vec3_t(), p1 = new CCDVec3.ccd_vec3_t(), p2 = new CCDVec3.ccd_vec3_t();

	    portalDir(portal, dir);

	    // use barycentric coordinates of tetrahedron to find origin
	    CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint1(portal).v,
	                       ccdSimplexPoint2(portal).v);
	    b0 = CCDVec3.ccdVec3Dot(vec, ccdSimplexPoint3(portal).v);

	    CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint3(portal).v,
	                       ccdSimplexPoint2(portal).v);
	    b1 = CCDVec3.ccdVec3Dot(vec, ccdSimplexPoint0(portal).v);

	    CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint0(portal).v,
	                       ccdSimplexPoint1(portal).v);
	    b2 = CCDVec3.ccdVec3Dot(vec, ccdSimplexPoint3(portal).v);

	    CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint2(portal).v,
	                       ccdSimplexPoint1(portal).v);
	    b3 = CCDVec3.ccdVec3Dot(vec, ccdSimplexPoint0(portal).v);

		sum = b0 + b1 + b2 + b3;

	    if (CCDVec3.ccdIsZero(sum) || sum < CCDVec3.CCD_ZERO){
			b0 = (0.);

	        CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint2(portal).v,
	                           ccdSimplexPoint3(portal).v);
	        b1 = CCDVec3.ccdVec3Dot(vec, dir);
	        CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint3(portal).v,
	                           ccdSimplexPoint1(portal).v);
	        b2 = CCDVec3.ccdVec3Dot(vec, dir);
	        CCDVec3.ccdVec3Cross(vec, ccdSimplexPoint1(portal).v,
	                           ccdSimplexPoint2(portal).v);
	        b3 = CCDVec3.ccdVec3Dot(vec, dir);

			sum = b1 + b2 + b3;
		}

		inv = (1.) / sum;

	    CCDVec3.ccdVec3Copy(p1, CCDVec3.ccd_vec3_origin);
	    CCDVec3.ccdVec3Copy(p2, CCDVec3.ccd_vec3_origin);
	    //for (i = 0; i < 4; i++){
	    //0
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint0(portal).v1);
	    CCDVec3.ccdVec3Scale(vec, b0);
	    CCDVec3.ccdVec3Add(p1, vec);
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint0(portal).v2);
	    CCDVec3.ccdVec3Scale(vec, b0);
	    CCDVec3.ccdVec3Add(p2, vec);
	    //1
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint1(portal).v1);
	    CCDVec3.ccdVec3Scale(vec, b1);
	    CCDVec3.ccdVec3Add(p1, vec);
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint1(portal).v2);
	    CCDVec3.ccdVec3Scale(vec, b1);
	    CCDVec3.ccdVec3Add(p2, vec);
	    //2
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint2(portal).v1);
	    CCDVec3.ccdVec3Scale(vec, b2);
	    CCDVec3.ccdVec3Add(p1, vec);
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint2(portal).v2);
	    CCDVec3.ccdVec3Scale(vec, b2);
	    CCDVec3.ccdVec3Add(p2, vec);
	    //3
	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint3(portal).v1);
	    CCDVec3.ccdVec3Scale(vec, b3);
	    CCDVec3.ccdVec3Add(p1, vec);

	    CCDVec3.ccdVec3Copy(vec, ccdSimplexPoint3(portal).v2);
	    CCDVec3.ccdVec3Scale(vec, b3);
	    CCDVec3.ccdVec3Add(p2, vec);

		//}
	    CCDVec3.ccdVec3Scale(p1, inv);
	    CCDVec3.ccdVec3Scale(p2, inv);

	    CCDVec3.ccdVec3Copy(pos, p1);
	    CCDVec3.ccdVec3Add(pos, p2);
	    CCDVec3.ccdVec3Scale(pos, 0.5);
	}

	
	/** Extends portal with new support point.
	 *  Portal must have face v1-v2-v3 arranged to face outside portal. */
	private static final void expandPortal(ccd_simplex_t portal,
	                              final CCDSupport.ccd_support_t v4)
	{
	    double dot;
	    final CCDVec3.ccd_vec3_t v4v0 = new CCDVec3.ccd_vec3_t();

	    CCDVec3.ccdVec3Cross(v4v0, v4.v, ccdSimplexPoint0(portal).v);
	    dot = CCDVec3.ccdVec3Dot(ccdSimplexPoint1(portal).v, v4v0);
	    if (dot > CCDVec3.CCD_ZERO){
	        dot = CCDVec3.ccdVec3Dot(ccdSimplexPoint2(portal).v, v4v0);
	        if (dot > CCDVec3.CCD_ZERO){
	            ccdSimplexSet1(portal, v4);
	        }else{
	            ccdSimplexSet3(portal, v4);
	        }
	    }else{
	        dot = CCDVec3.ccdVec3Dot(ccdSimplexPoint3(portal).v, v4v0);
	        if (dot > CCDVec3.CCD_ZERO){
	            ccdSimplexSet2(portal, v4);
	        }else{
	            ccdSimplexSet1(portal, v4);
	        }
	    }
	}


	/** Fill dir with direction outside portal. Portal's v1-v2-v3 face must be
	 *  arranged in correct order! */
	private static final void portalDir(final ccd_simplex_t portal, CCDVec3.ccd_vec3_t dir)
	{
	    final CCDVec3.ccd_vec3_t v2v1 = new CCDVec3.ccd_vec3_t(), v3v1 = new CCDVec3.ccd_vec3_t();

	    CCDVec3.ccdVec3Sub2(v2v1, ccdSimplexPoint2(portal).v,
	                       ccdSimplexPoint1(portal).v);
	    CCDVec3.ccdVec3Sub2(v3v1, ccdSimplexPoint3(portal).v,
	                       ccdSimplexPoint1(portal).v);
	    CCDVec3.ccdVec3Cross(dir, v2v1, v3v1);
	    CCDVec3.ccdVec3Normalize(dir);
	}

	/** Returns true if portal encapsules origin (0,0,0), dir is direction of
	 *  v1-v2-v3 face. */
	private static final boolean portalEncapsulesOrigin(final ccd_simplex_t portal,
	                                       final CCDVec3.ccd_vec3_t dir)
	{
	    double dot;
	    dot = CCDVec3.ccdVec3Dot(dir, ccdSimplexPoint1(portal).v);
	    return CCDVec3.ccdIsZero(dot) || dot > CCDVec3.CCD_ZERO;
	}

	/** Returns true if portal with new point v4 would reach specified
	 *  tolerance (i.e. returns true if portal can _not_ significantly expand
	 *  within Minkowski difference).
	 *
	 *  v4 is candidate for new point in portal, dir is direction in which v4
	 *  was obtained. */
	private static final boolean portalReachTolerance(final ccd_simplex_t portal,
	                                     final CCDSupport.ccd_support_t v4,
	                                     final CCDVec3.ccd_vec3_t dir,
	                                     final ccd_t ccd)
	{
	    double dv1, dv2, dv3, dv4;
	    double dot1, dot2, dot3;

	    // find the smallest dot product of dir and {v1-v4, v2-v4, v3-v4}

	    dv1 = CCDVec3.ccdVec3Dot(ccdSimplexPoint1(portal).v, dir);
	    dv2 = CCDVec3.ccdVec3Dot(ccdSimplexPoint2(portal).v, dir);
	    dv3 = CCDVec3.ccdVec3Dot(ccdSimplexPoint3(portal).v, dir);
	    dv4 = CCDVec3.ccdVec3Dot(v4.v, dir);

	    dot1 = dv4 - dv1;
	    dot2 = dv4 - dv2;
	    dot3 = dv4 - dv3;

	    dot1 = CCDVec3.CCD_FMIN(dot1, dot2);
	    dot1 = CCDVec3.CCD_FMIN(dot1, dot3);

	    return CCDVec3.ccdEq(dot1, ccd.mpr_tolerance) || dot1 < ccd.mpr_tolerance;
	}

	/** Returns true if portal expanded by new point v4 could possibly contain
	 *  origin, dir is direction in which v4 was obtained. */
	private static final boolean portalCanEncapsuleOrigin(final ccd_simplex_t portal,   
	                                         final CCDSupport.ccd_support_t v4,
	                                         final CCDVec3.ccd_vec3_t dir)
	{
	    double dot;
	    dot = CCDVec3.ccdVec3Dot(v4.v, dir);
	    return CCDVec3.ccdIsZero(dot) || dot > CCDVec3.CCD_ZERO;
	}

}
