/***
 * libccd
 * ---------------------------------
 * Copyright (c)2010 Daniel Fiser <danfis@danfis.cz>
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

import com.github.antzGames.gdx.ode4j.ode.internal.libccd.CCD.ccd_t;

/**
 * 
 *
 * LibCCD class for support points.
 */
public class CCDSupport {

	/**
	 * Support point type.
	 */
	public static final class ccd_support_t {
		final CCDVec3.ccd_vec3_t v = new CCDVec3.ccd_vec3_t();  //!< Support point in minkowski sum
		final CCDVec3.ccd_vec3_t v1 = new CCDVec3.ccd_vec3_t(); //!< Support point in obj1
		final CCDVec3.ccd_vec3_t v2 = new CCDVec3.ccd_vec3_t(); //!< Support point in obj2
		/**
		 * @return Support point minkowski sum.
		 */
		public CCDVec3.ccd_vec3_t v() {
			return v;
		}
	};

	/**
	 * Computes support point of obj1 and obj2 in direction dir.
	 * Support point is returned via supp.
	 */
	static final void __ccdSupport(final Object obj1, final Object obj2,
                                   final CCDVec3.ccd_vec3_t _dir, final ccd_t ccd,
                                   final ccd_support_t supp)
	{
		CCDVec3.ccd_vec3_t dir = new CCDVec3.ccd_vec3_t();

		CCDVec3.ccdVec3Copy(dir, _dir);

		ccd.support1.run(obj1, dir, supp.v1);

		CCDVec3.ccdVec3Scale(dir, -CCDVec3.CCD_ONE);
		ccd.support2.run(obj2, dir, supp.v2);

		CCDVec3.ccdVec3Sub2(supp.v, supp.v1, supp.v2);
	}


	/**** INLINES ****/
	static final void ccdSupportCopy(ccd_support_t d, final ccd_support_t s)
	{
		//*d = *s;
		CCDVec3.ccdVec3Copy(d.v, s.v);
		CCDVec3.ccdVec3Copy(d.v1, s.v1);
		CCDVec3.ccdVec3Copy(d.v2, s.v2);
	}


}
