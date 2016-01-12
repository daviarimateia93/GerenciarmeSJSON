package me.gerenciar.sjson.using;

public class Response
{
	public static enum Status
	{
		OK, ERROR
	}
	
	private Status status;
	
	public Response()
	{
	
	}
	
	public Response(Status status)
	{
		this.status = status;
	}
	
	public Status getStatus()
	{
		return status;
	}
}
