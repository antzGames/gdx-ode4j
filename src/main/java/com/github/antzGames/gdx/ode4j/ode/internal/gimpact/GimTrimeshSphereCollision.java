/**
 * ----------------------------------------------------------------------------
 * This source file is part of the ODE4J library (ported to
 * Java from the GIMPACT Library).
 * 
 * For the latest info on ODE4J, see http://www.ode4j.org/
 * For the latest info on GIMPACT, see http://gimpact.sourceforge.net/
 * 
 * Copyright of GIMPACT (c) 2006 Francisco Leon. C.C. 80087371.
 * email: projectileman@yahoo.com
 * Copyright of ODE4J (c) 2009-2014 Tilmann Zäschke.
 * email: ode4j.gmx.de
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of EITHER:
 *   (1) The GNU Lesser General Public License as published by the Free
 *       Software Foundation; either version 2.1 of the License, or (at
 *       your option) any later version. The text of the GNU Lesser
 *       General Public License is included with this library in the
 *       file GIMPACT-LICENSE-LGPL.TXT and LICENSE.TXT.
 *   (2) The BSD-style license that is included with this library in
 *       the file GIMPACT-LICENSE-BSD.TXT and ODE4J-LICENSE-BSD.TXT.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files
 * GIMPACT-LICENSE-LGPL.TXT, GIMPACT-LICENSE-BSD.TXT, LICENSE.TXT and 
 * ODE4J-LICENSE-BSD.TXT for more details.
 * 
 * ----------------------------------------------------------------------------
 */
package com.github.antzGames.gdx.ode4j.ode.internal.gimpact;

/**
 * Ported to Java by Tilmann Zaeschke
 * @author Francisco Leon
*/
public class GimTrimeshSphereCollision {


//	int gim_triangle_sphere_collision(
//								GIM_TRIANGLE_DATA *tri,
//								vec3f center, GREAL radius,
//								GIM_TRIANGLE_CONTACT_DATA * contact_data)
	static boolean gim_triangle_sphere_collision(
            final GimTriCollision.GIM_TRIANGLE_DATA tri,
            final GimGeometry.vec3f center, final float radius,
            final GimTriCollision.GIM_TRIANGLE_CONTACT_DATA contact_data)
	{
	    contact_data.m_point_count = 0;

	    //Find Face plane distance
	    float  dis = GimGeometry.DISTANCE_PLANE_POINT(tri.m_planes.m_planes[0],center);
	    if(dis>radius) return false; //out
	    if(dis<-radius) return false;//Out of triangle
	    contact_data.m_penetration_depth = dis;

	    //Find the most edge
	    int most_edge = 4;//no edge
	    float max_dis = 0.0f;
	    dis = GimGeometry.DISTANCE_PLANE_POINT(tri.m_planes.m_planes[1],center);
	    if(dis>radius) return false;//Out of triangle
	    if(dis>0.0f)
	    {
	        max_dis = dis;
	        most_edge = 0;
	    }

	    dis = GimGeometry.DISTANCE_PLANE_POINT(tri.m_planes.m_planes[2],center);
	    if(dis>radius) return false;//Out of triangle
	    if(dis>max_dis)// && dis>0.0f)
	    {
	        max_dis = dis;
	        most_edge = 1;
	    }

	    dis = GimGeometry.DISTANCE_PLANE_POINT(tri.m_planes.m_planes[3],center);
	    if(dis>radius) return false;//Out of triangle
	    if(dis>max_dis)// && dis>0.0f)
	    {
	        max_dis = dis;
	        most_edge = 2;
	    }

	    if(most_edge == 4) //Box is into triangle
	    {
	        //contact_data.m_penetration_depth = dis is set above
	        //Find Face plane point
	        GimGeometry.VEC_COPY(contact_data.m_separating_normal,tri.m_planes.m_planes[0]);
	        //Find point projection on plane
	        if(contact_data.m_penetration_depth>=0.0f)
	        {
	            GimGeometry.VEC_SCALE(contact_data.m_points[0],-radius,contact_data.m_separating_normal);
	        }
	        else
	        {
	            GimGeometry.VEC_SCALE(contact_data.m_points[0],radius,contact_data.m_separating_normal);
	        }
	        contact_data.m_penetration_depth = radius - contact_data.m_penetration_depth;

	        GimGeometry.VEC_SUM(contact_data.m_points[0],contact_data.m_points[0],center);
	        //Scale normal for pointing to triangle
	        GimGeometry.VEC_SCALE(contact_data.m_separating_normal,-1.0f,contact_data.m_separating_normal);
	        contact_data.m_point_count = 1;
	        return true;
	    }
	    //find the edge
	    GimGeometry.vec3f e1 = new GimGeometry.vec3f(), e2 = new GimGeometry.vec3f();
	    GimGeometry.VEC_COPY(e1,tri.m_vertices[most_edge]);
	    GimGeometry.VEC_COPY(e2,tri.m_vertices[(most_edge+1)%3]);

	    GimGeometry.CLOSEST_POINT_ON_SEGMENT(contact_data.m_points[0],center,e1,e2);
	    //find distance
	    GimGeometry.VEC_DIFF(e1,center,contact_data.m_points[0]);
	    dis = GimGeometry.VEC_LENGTH(e1);
	    if(dis>radius) return false;

	    contact_data.m_penetration_depth = radius - dis;

	    if(GimMath.IS_ZERO(dis))
	    {
	        GimGeometry.VEC_COPY(contact_data.m_separating_normal,tri.m_planes.m_planes[most_edge+1]);
	        GimGeometry.VEC_SCALE(contact_data.m_points[0],-radius,contact_data.m_separating_normal);
	        GimGeometry.VEC_SUM(contact_data.m_points[0],contact_data.m_points[0],center);
	    }
	    else
	    {
	        GimGeometry.VEC_SCALE(contact_data.m_separating_normal,1.0f/dis,e1);
	        GimGeometry.VEC_SCALE(contact_data.m_points[0],-radius,contact_data.m_separating_normal);
	        GimGeometry.VEC_SUM(contact_data.m_points[0],contact_data.m_points[0],center);
	    }

	    //Scale normal for pointing to triangle
	    GimGeometry.VEC_SCALE(contact_data.m_separating_normal,-1.0f,contact_data.m_separating_normal);

	    contact_data.m_point_count = 1;
	    return true;

	}

	/**
	 * Trimesh Sphere Collisions.
	 * 
	 * In each contact:
	 * <ul>
	 * <li> m_handle1 points to trimesh.
	 * <li> m_handle2 points to NULL.
	 * <li> m_feature1 Is a triangle index of trimesh.
	 * </ul>
	 * 
	 * @param trimesh
	 * @param center
	 * @param radius
	 * @param contacts A GIM_CONTACT array. Must be initialized
	 */
//	void gim_trimesh_sphere_collision(GIM_TRIMESH * trimesh,vec3f center,GREAL radius, GDYNAMIC_ARRAY * contacts)
//	{
	static void gim_trimesh_sphere_collision(final GimTrimesh trimesh, final GimGeometry.vec3f center,
			final float radius, final GimDynArray<GimContact> contacts)
	{
	    contacts.m_size = 0;

	    GimGeometry.aabb3f test_aabb = new GimGeometry.aabb3f();
		test_aabb.minX = center.f[0]-radius;
		test_aabb.maxX = center.f[0]+radius;
		test_aabb.minY = center.f[1]-radius;
		test_aabb.maxY = center.f[1]+radius;
		test_aabb.minZ = center.f[2]-radius;
		test_aabb.maxZ = center.f[2]+radius;

		GimDynArrayInt collision_result = GimDynArrayInt.GIM_CREATE_BOXQUERY_LIST();

		trimesh.m_aabbset.gim_aabbset_box_collision(test_aabb, collision_result);

		if(collision_result.size()==0)
		{
			collision_result.GIM_DYNARRAY_DESTROY();
		}

		//collide triangles
		//Locks trimesh
		trimesh.gim_trimesh_locks_work_data();
		 //dummy contacts
	    GimDynArray<GimContact> dummycontacts = GimContact.GIM_CREATE_CONTACT_LIST();

		boolean cresult;
		int[] boxesresult = collision_result.GIM_DYNARRAY_POINTER();
		GimTriCollision.GIM_TRIANGLE_CONTACT_DATA tri_contact_data = new GimTriCollision.GIM_TRIANGLE_CONTACT_DATA();
		GimTriCollision.GIM_TRIANGLE_DATA tri_data = new GimTriCollision.GIM_TRIANGLE_DATA();

		for(int i=0;i<collision_result.size();i++)
		{
			trimesh.gim_trimesh_get_triangle_data(boxesresult[i],tri_data);
			cresult = gim_triangle_sphere_collision(tri_data,center,radius,tri_contact_data);
			if(cresult!=false)
			{
				GimContact.GIM_PUSH_CONTACT(dummycontacts,tri_contact_data.m_points[0],
						tri_contact_data.m_separating_normal,
						tri_contact_data.m_penetration_depth,trimesh, null, boxesresult[i],0);
			}
		}
		///unlocks
		trimesh.gim_trimesh_unlocks_work_data();
		///Destroy box result
		collision_result.GIM_DYNARRAY_DESTROY();

		 //merge contacts
	    GimContact.gim_merge_contacts(dummycontacts,contacts);

	    //Destroy dummy
	    dummycontacts.GIM_DYNARRAY_DESTROY();
	}
}
