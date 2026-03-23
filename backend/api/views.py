from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
import json

from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession
from .serializers import (
    AnnualGoalSerializer,
    AuthorSerializer,
    GenreSerializer,
    BookSerializer,
    ReadingSerializer,
    ReadingSessionSerializer
)

class BookViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Book.objects.all()
    serializer_class = BookSerializer

class AnnualGoalViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = AnnualGoal.objects.all()
    serializer_class = AnnualGoalSerializer

    def get_queryset(self):
        user = self.request.user
        return AnnualGoal.objects.filter(user=user)

class AuthorViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer

class GenreViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Genre.objects.all()
    serializer_class = GenreSerializer



@csrf_exempt
def readings(request):
    if request.method == 'GET':
        user_id = request.GET.get('user', None)

        readings = Reading.objects.all()
        if user_id:
            readings = readings.filter(user_id=user_id)

        response = []
        for r in readings:
            response.append({
                'id': r.id,
                'book': r.book.title,
                'status': r.status,
                'start_date': r.start_date,
                'end_date': r.end_date
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        body = json.loads(request.body)

        reading = Reading.objects.create(
            user_id=body['user'],
            book_id=body['book'],
            status=body['status']
        )

        return JsonResponse({'id': reading.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_by_id(request, id):
    try:
        reading = Reading.objects.get(id=id)
    except Reading.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': reading.id,
            'book': reading.book.title,
            'status': reading.status
        })

    elif request.method == 'PUT':
        body = json.loads(request.body)

        reading.status = body.get('status', reading.status)
        reading.save()

        return JsonResponse({'updated': True})

    elif request.method == 'DELETE':
        reading.delete()
        return JsonResponse({'deleted': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

class ReadingSessionViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = ReadingSession.objects.all()
    serializer_class = ReadingSessionSerializer

    def get_queryset(self):
        return ReadingSession.objects.filter(reading__user=self.request.user)



