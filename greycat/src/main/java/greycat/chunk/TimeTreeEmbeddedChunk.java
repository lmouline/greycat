package greycat.chunk;

public interface TimeTreeEmbeddedChunk extends TimeTreeChunk {

    StateChunk state(int offset);

}
