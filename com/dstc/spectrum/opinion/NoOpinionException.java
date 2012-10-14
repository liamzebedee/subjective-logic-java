package com.dstc.spectrum.opinion;

public class NoOpinionException extends Exception
{
  private static final long serialVersionUID = 1L;

  public NoOpinionException()
  {
    super("No opinion could be found.");
  }
}