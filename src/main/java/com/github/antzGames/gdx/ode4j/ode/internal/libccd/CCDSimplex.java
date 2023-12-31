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

/**
 *
 * LibCCD simplex class.
 */
public class CCDSimplex {


	static class ccd_simplex_t {
//		private final ccd_support_t[] ps = new ccd_support_t[4];
		int last; //!< index of last added point
//		{
//			ps[0] = new ccd_support_t();
//			ps[1] = new ccd_support_t();
//			ps[2] = new ccd_support_t();
//			ps[3] = new ccd_support_t();
//		}
		private final CCDSupport.ccd_support_t ps0 = new CCDSupport.ccd_support_t();
		private final CCDSupport.ccd_support_t ps1 = new CCDSupport.ccd_support_t();
		private final CCDSupport.ccd_support_t ps2 = new CCDSupport.ccd_support_t();
		private final CCDSupport.ccd_support_t ps3 = new CCDSupport.ccd_support_t();
	};


	/**** INLINES ****/

	static final void ccdSimplexInit(ccd_simplex_t s)
	{
		s.last = -1;
	}

	static final int ccdSimplexSize(final ccd_simplex_t s)
	{
		return s.last + 1;
	}

	static final CCDSupport.ccd_support_t ccdSimplexLast(final ccd_simplex_t s)
	{
		//return ccdSimplexPoint(s, s.last);
		switch (s.last){
		case 0: return ccdSimplexPoint0(s);
		case 1: return ccdSimplexPoint1(s);
		case 2: return ccdSimplexPoint2(s);
		case 3: return ccdSimplexPoint3(s);
		}
		throw new IllegalArgumentException();
	}

//	static final ccd_support_t ccdSimplexPoint(final ccd_simplex_t s, int idx)
//	{
//		// here is no check on boundaries
//		return s.ps[idx];
//	}
	static final CCDSupport.ccd_support_t ccdSimplexPoint0(final ccd_simplex_t s) {
		return s.ps0;
	}
	static final CCDSupport.ccd_support_t ccdSimplexPoint1(final ccd_simplex_t s) {
		return s.ps1;
	}
	static final CCDSupport.ccd_support_t ccdSimplexPoint2(final ccd_simplex_t s) {
		return s.ps2;
	}
	static final CCDSupport.ccd_support_t ccdSimplexPoint3(final ccd_simplex_t s) {
		return s.ps3;
	}
//	static final ccd_support_t ccdSimplexPointW(ccd_simplex_t s, int idx)
//	{
//		return s.ps[idx];
//	}
	static final CCDSupport.ccd_support_t ccdSimplexPointW0(final ccd_simplex_t s) {
		return s.ps0;
	}
	static final CCDSupport.ccd_support_t ccdSimplexPointW1(final ccd_simplex_t s) {
		return s.ps1;
	}
	static final CCDSupport.ccd_support_t ccdSimplexPointW2(final ccd_simplex_t s) {
		return s.ps2;
	}
	static final CCDSupport.ccd_support_t ccdSimplexPointW3(final ccd_simplex_t s) {
		return s.ps3;
	}

	static final void ccdSimplexAdd(ccd_simplex_t s, final CCDSupport.ccd_support_t v)
	{
		// here is no check on boundaries in sake of speed
		++s.last;
		//ccdSupportCopy(s.ps[s.last], v);
		switch(s.last){
		case 0: CCDSupport.ccdSupportCopy(s.ps0, v); break;
		case 1: CCDSupport.ccdSupportCopy(s.ps1, v); break;
		case 2: CCDSupport.ccdSupportCopy(s.ps2, v); break;
		case 3: CCDSupport.ccdSupportCopy(s.ps3, v); break;
		default: throw new IllegalArgumentException();
		}
	}

//	static final void ccdSimplexSet(ccd_simplex_t s, int pos, final ccd_support_t a)
//	{
//		ccdSupportCopy(s.ps[pos], a);
//	}
	static final void ccdSimplexSet0(ccd_simplex_t s, final CCDSupport.ccd_support_t a) {
		CCDSupport.ccdSupportCopy(s.ps0, a);
	}
	static final void ccdSimplexSet1(ccd_simplex_t s, final CCDSupport.ccd_support_t a) {
		CCDSupport.ccdSupportCopy(s.ps1, a);
	}
	static final void ccdSimplexSet2(ccd_simplex_t s, final CCDSupport.ccd_support_t a) {
		CCDSupport.ccdSupportCopy(s.ps2, a);
	}
	static final void ccdSimplexSet3(ccd_simplex_t s, final CCDSupport.ccd_support_t a) {
		CCDSupport.ccdSupportCopy(s.ps3, a);
	}

	static final void ccdSimplexSetSize(ccd_simplex_t s, int size)
	{
		s.last = size - 1;
	}

//	static final void ccdSimplexSwap(ccd_simplex_t s, int pos1, int pos2)
//	{
//		ccd_support_t supp = new ccd_support_t();
//
//		ccdSupportCopy(supp, s.ps[pos1]);
//		ccdSupportCopy(s.ps[pos1], s.ps[pos2]);
//		ccdSupportCopy(s.ps[pos2], supp);
//	}
	/**
	 * swaps position 1 and 2.
	 */
	static final void ccdSimplexSwap12(ccd_simplex_t s)
	{
		CCDSupport.ccd_support_t supp = new CCDSupport.ccd_support_t();

		CCDSupport.ccdSupportCopy(supp, s.ps1);
		CCDSupport.ccdSupportCopy(s.ps1, s.ps2);
		CCDSupport.ccdSupportCopy(s.ps2, supp);
	}

}