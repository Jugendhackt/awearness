#!/usr/bin/env python

import json, urllib.request, pycouchdb, argparse

tag = 'man_made=surveillance'
couchdb_server = '127.0.0.1:5984'
couchdb_db = 'cctv1'
couchdb_user = 'test'
couchdb_password = 'musterPW'

arg = argparse.ArgumentParser()
arg.add_argument('couchdb', help='The CouchDB-Installation you wan\'t to use in the form user:passwort@server:port')
arg.add_argument('database', help='The CouchDB-Database you wan\'t to use')
arg.add_argument('tag', help='The OpenStreetMap tag which you wan\'t to use to get the data')
arg.parse_args()

query = '[out:json];node[' + arg.tag + '];out;'
print('Try to get the data for',tag,'from OpenStreetMap')

req = urllib.request.Request(url='http://overpass.osm.rambler.ru/cgi/interpreter', data=query.encode(encoding='utf-8'))
resp = urllib.request.urlopen(req)

if resp.status == 200:
	print('Starting load data into CouchDB')
	data = resp.read().decode('utf-8')
	data = json.loads(data)

	server = pycouchdb.Server('http://' + arg.couchdb)
	db = server.database(arg.database)
	for element in data['elements']:
		doc = dict(lon = element['lon'], lat = element['lat'])
		db_resp = db.save(doc)
		print('Created document:',db_resp['_id'])

else:
	print('Error: Could not get data from OpenStreetMap!\n',resp.status,'\n',resp.reason)
