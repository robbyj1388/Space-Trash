.ONESHELL: # use one shell for commands
SHELL := /bin/bash

run:
	mvn clean javafx:run &           # run server in background
	sleep 10                          # give server time to start
	source venv/bin/activate
	python hand_tracking.py
