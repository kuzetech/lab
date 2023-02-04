package com.kuze.bigdata.study;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pipeline")
@Produces(MediaType.APPLICATION_JSON)
public class PipelineRESTController {

    // 如果访问路径为 /pipeline/ 会访问到该方法
    @GET
    public String get() throws Exception {
        throw new Exception("指定的书目不存在");
    }

    @GET
    @Path("/{id}")
    public Response getPipelineById(@PathParam("id") Integer id) {
        return Response.ok(id).build();
    }

    @GET
    @Path("/list")
    public Response getPipelines() {
        return Response.ok("list").build();
    }

    @POST
    public Response createPipeline(@Valid Pipeline Pipeline) {
        return Response.ok("create success").build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePipelineById(@PathParam("id") Integer id, Pipeline Pipeline) {
        return Response.ok("update " + id).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removePipelineById(@PathParam("id") Integer id) {
        return Response.ok("remove " + id).build();
    }

}
