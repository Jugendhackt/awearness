#!/usr/bin/env python


import json, urllib.request, pycouchdb

tag = 'man_made=surveillance'
couchdb_server = '127.0.0.1:5984'
couchdb_db = 'cctv1'
couchdb_user = 'test'
couchdb_password = 'musterPW'

query = '[out:json];node[' + tag + '];out;'
print('Try to get the data for',tag,'from OpenStreetMap')

req = urllib.request.Request(url='http://overpass.osm.rambler.ru/cgi/interpreter', data=query.encode(encoding='utf-8'))
resp = urllib.request.urlopen(req)

if resp.status == 200:
	print('Starting load data into CouchDB')
	data = resp.read().decode('utf-8')
	data = json.loads(data)

	server = pycouchdb.Server('http://' + couchdb_user + ':' + couchdb_password + '@' + couchdb_server)
	db = server.database(couchdb_db)
	for element in data['elements']:
		doc = dict(lon = element['lon'], lat = element['lat'])
		db_resp = db.save(doc)
#		db_resp = resp.decode('utf-8')
#		db_resp = json.loads(resp)
		print('Created document:',db_resp['_id'])

else:
	print('Error: Could not get data from OpenStreetMap!\n',resp.status,'\n',resp.reason)
