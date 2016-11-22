package it.flipb.theapp.infrastructure.service.dispatcher;

public interface CommandDispatcher {
    byte[] dispatch(String _command, String[] _arguments, String _outputFile);
}
